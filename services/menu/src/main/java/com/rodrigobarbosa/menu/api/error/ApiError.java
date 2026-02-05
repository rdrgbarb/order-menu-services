package com.rodrigobarbosa.menu.api.error;

import java.time.OffsetDateTime;
import java.util.List;

public record ApiError(
    OffsetDateTime timestamp,
    int status,
    String error,
    String message,
    String path,
    List<ApiErrorDetail> details
) {
    public record ApiErrorDetail(String field, String message) {
    }
}
