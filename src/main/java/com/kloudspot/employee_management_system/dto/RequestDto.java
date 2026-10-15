package com.kloudspot.employee_management_system.dto;

import com.kloudspot.employee_management_system.entity.EmployeeStatus;
import jakarta.validation.constraints.*;

import java.time.LocalDate;

public record RequestDto(
        @NotBlank(message = "Full name is required")
        @Size(min = 3, max = 100, message = "Name must be 3 to 100 characters") String fullName,
        @NotBlank(message = "Email is required") @Email(message = "Invalid email") String email,
        @NotBlank(message = "Phone is required")
        @Pattern(regexp = "^[6-9][0-9]{9}$", message = "Phone must be 10 digits and start with 6-9") String phoneNo,
        @NotBlank(message = "Department is required") String department,
        @NotBlank(message = "Designation is required") String designation,
        @NotNull(message = "Date of joining is required")
        @PastOrPresent(message = "Date of joining cannot be in the future") LocalDate dateOfJoining,
        @Positive(message = "Salary must be positive") double salary,
        @NotNull(message = "Status is required") EmployeeStatus status
) {}
