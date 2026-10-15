package com.kloudspot.employee_management_system.dto;

import com.kloudspot.employee_management_system.entity.EmployeeStatus;

import java.time.LocalDate;

public record ResponseDto (

        int employeeId, String fullName, String email, String phoneNo,
        String department, String designation, LocalDate dateOfJoining,
        double salary, EmployeeStatus status
){}
