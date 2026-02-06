package com.rodrigobarbosa.order.api.dto;

import java.util.List;

public record OrderHistoryResponse<T>(long totalRecords, List<T> orderItems) {}
