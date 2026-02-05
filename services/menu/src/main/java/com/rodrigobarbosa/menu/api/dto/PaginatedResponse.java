package com.rodrigobarbosa.menu.api.dto;

import java.util.List;

public record PaginatedResponse<T>(long totalRecords, List<T> items) {}
