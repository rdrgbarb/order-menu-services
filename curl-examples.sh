#!/usr/bin/env bash
# -----------------------------------------------------------------------------
# curl-examples.sh — End-to-end smoke demo (Menu + Order + RabbitMQ)
#
# What it proves (thin vertical slice):
#   1) menu is reachable (health)
#   2) menu item creation + listing works (Mongo persistence)
#   3) order creation works and depends on menu (HTTP client enrichment + snapshot)
#   4) status update publishes a RabbitMQ event
#   5) order service consumes the event and logs a NOTIFICATION (simulation)
#
# Requirements:
#   - curl
#   - jq
#
# Usage:
#   chmod +x ./curl-examples.sh
#   ./curl-examples.sh
#
# Useful knobs (env vars):
#   MENU_COUNT=5        # number of menu items to create (default: 2)
#   ORDER_COUNT=50      # number of orders to create (default: 1)
#   PATCH_STATUS=PREPARING
#   OFFSET=0 LIMIT=20   # pagination for GET /orders
#   MENU_BASE_URL=http://localhost:8081
#   ORDER_BASE_URL=http://localhost:8082
#
# Notes:
#   - Prices are generated using integer "cents" to avoid locale issues (e.g. comma decimals).
#   - This is a demo/smoke script, not a performance test.
# -----------------------------------------------------------------------------

set -euo pipefail

MENU_BASE_URL="${MENU_BASE_URL:-http://localhost:8081}"
ORDER_BASE_URL="${ORDER_BASE_URL:-http://localhost:8082}"

MENU_COUNT="${MENU_COUNT:-2}"
ORDER_COUNT="${ORDER_COUNT:-1}"
PATCH_STATUS="${PATCH_STATUS:-PREPARING}"

OFFSET="${OFFSET:-0}"
LIMIT="${LIMIT:-20}"

need() { command -v "$1" >/dev/null 2>&1 || { echo "Missing dependency: $1"; exit 1; }; }
need curl
need jq

hr() { echo "------------------------------------------------------------"; }
say() { echo -e "\n👉 $*"; }

curl_json() {
  # curl_json METHOD URL  (body from stdin)
  local method="$1"; shift
  local url="$1"; shift
  curl -fsS -X "$method" "$url" \
    -H 'Content-Type: application/json' \
    --data-binary @-
}

# 1) Health
hr
say "1) Health checks"
curl -fsS "${MENU_BASE_URL}/actuator/health" >/dev/null && echo "✅ menu healthy"
curl -fsS "${ORDER_BASE_URL}/actuator/health" >/dev/null && echo "✅ order healthy"

# 2) Create menu items
hr
say "2) Create menu items (MENU_COUNT=${MENU_COUNT})"
MENU_IDS=()

for i in $(seq 1 "${MENU_COUNT}"); do
  name="Item ${i}"

  # Locale-proof price generation using integer cents:
  # base = 5.00 (500 cents), step = 1.25 (125 cents) * i
  price_cents=$((500 + i * 125))
  price="$(printf "%d.%02d" $((price_cents / 100)) $((price_cents % 100)))"

  payload="$(
    jq -n \
      --arg name "$name" \
      --arg description "Demo item ${i}" \
      --arg price "$price" \
      '{name:$name, price:($price|tonumber), description:$description}'
  )"

  resp="$(printf '%s' "$payload" | curl_json POST "${MENU_BASE_URL}/menu-items")"

  echo "$resp" | jq .
  MENU_IDS+=("$(echo "$resp" | jq -r '.id')")
done

echo "✅ menu ids: ${MENU_IDS[*]}"

# 3) List menu items
hr
say "3) List menu items"
curl -fsS "${MENU_BASE_URL}/menu-items?limit=50&offset=0" | jq .

# 4) Create orders (round-robin menu ids)
hr
say "4) Create orders (ORDER_COUNT=${ORDER_COUNT})"
ORDER_IDS=()

pick_menu_id() {
  local idx="$1"
  local pos=$(( (idx - 1) % ${#MENU_IDS[@]} ))
  echo "${MENU_IDS[$pos]}"
}

for i in $(seq 1 "${ORDER_COUNT}"); do
  product_id="$(pick_menu_id "$i")"
  customer_name="John Doe ${i}"
  email="john${i}@example.com"

  payload="$(
    jq -n \
      --arg fullName "$customer_name" \
      --arg address "123 Main St" \
      --arg email "$email" \
      --arg productId "$product_id" \
      '{
        customer: { fullName: $fullName, address: $address, email: $email },
        orderItems: [ { productId: $productId, quantity: 2 } ]
      }'
  )"

  resp="$(printf '%s' "$payload" | curl_json POST "${ORDER_BASE_URL}/orders")"

  echo "$resp" | jq .
  ORDER_IDS+=("$(echo "$resp" | jq -r '.id')")
done

echo "✅ order ids: ${ORDER_IDS[*]}"

# 5) Patch status for each order (M4 trigger)
hr
say "5) Patch status -> ${PATCH_STATUS} (publishes event)"
for id in "${ORDER_IDS[@]}"; do
  echo "PATCH /orders/${id}/status"
  payload="$(jq -n --arg status "$PATCH_STATUS" '{status:$status}')"
  printf '%s' "$payload" | curl_json PATCH "${ORDER_BASE_URL}/orders/${id}/status" | jq .
done

# 6) Get order by id (last one)
hr
say "6) Get last order by id"
last="${ORDER_IDS[-1]}"
curl -fsS "${ORDER_BASE_URL}/orders/${last}" | jq .

# 7) Get order history (paginated list)
hr
say "7) Get order history (GET /orders?offset=${OFFSET}&limit=${LIMIT})"
curl -fsS "${ORDER_BASE_URL}/orders?offset=${OFFSET}&limit=${LIMIT}" | jq .

# 8) Proof in logs
hr
say "8) Proof in logs (notification simulation)"
echo "Run:"
echo "  make logs.order FILTER=\"NOTIFICATION|Published eventType|order.status.changed\""
echo ""
echo "✅ Expect NOTIFICATION lines for status=${PATCH_STATUS}"
hr
echo "🎉 Done."
