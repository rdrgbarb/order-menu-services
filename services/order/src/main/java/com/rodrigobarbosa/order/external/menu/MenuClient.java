package com.rodrigobarbosa.order.external.menu;

import java.math.BigDecimal;
import java.util.Optional;

public interface MenuClient {
    Optional<MenuItem> getMenuItem(String productId);

    record MenuItem(String id, String name, BigDecimal price) {
    }

    class MenuUnavailableException extends RuntimeException {
        public MenuUnavailableException(String message, Throwable cause) {
            super(message, cause);
        }

        public MenuUnavailableException(String message) {
            super(message);
        }
    }
}
