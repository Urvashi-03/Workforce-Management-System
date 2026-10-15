package com.kloudspot.employee_management_system.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@Table(name = "Employee")
public class Employee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int employeeId;

    @NotBlank(message = "name is required")
    @Size(min = 3, max = 100, message = "name must be 3 to 100 characters")
    private String fullName;

    @NotBlank(message = "email is required")
    @Email(message = "email format is invalid")
    @Column(unique = true)
    private String email;

    @NotBlank(message = "phone number is required")
    @Pattern(regexp = "^[6-9][0-9]{9}$", message = "phone must be 10 digits and start with 6-9")
    private String phoneNo;

    @NotBlank(message = "department is required")
    private String department;

    @NotBlank(message = "designation is required")
    private String designation;

    @NotNull(message = "date is required")
    @PastOrPresent(message = "use present or past dates")
    private LocalDate dateOfJoining;

    @Positive(message = "salary must be positive")
    private double salary;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.VARCHAR)
    @Column(length = 20)
    @NotNull(message = "status is required")
    private EmployeeStatus status;
}
