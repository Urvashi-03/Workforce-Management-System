package com.kloudspot.employee_management_system.dto;

import java.util.List;

public record PageDto<T>(List<T> content, int page, int size,
                         long totalElements, int totalPages) {
}
