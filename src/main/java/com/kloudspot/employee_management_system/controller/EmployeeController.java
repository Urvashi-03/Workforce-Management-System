package com.kloudspot.employee_management_system.controller;

import com.kloudspot.employee_management_system.dto.PageDto;
import com.kloudspot.employee_management_system.dto.RequestDto;
import com.kloudspot.employee_management_system.dto.ResponseDto;
import com.kloudspot.employee_management_system.entity.EmployeeStatus;
import com.kloudspot.employee_management_system.service.EmployeeService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/employees")
@RequiredArgsConstructor
@Tag(name = "Employees", description = "Employee management APIs")
public class EmployeeController {

    private final EmployeeService employeeService;

    @GetMapping("/get")
    public ResponseEntity<PageDto<ResponseDto>> getAllEmployees(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String department,
            @RequestParam(required = false) EmployeeStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(
                employeeService.getAllEmployees(search, department, status, page, size));
    }

    @GetMapping("/get/{employeeId}")
    public ResponseEntity<ResponseDto> getEmployeeById(@PathVariable Integer employeeId) {
        return ResponseEntity.ok(employeeService.getEmployeeById(employeeId));
    }

    @PostMapping("/create")
    public ResponseEntity<ResponseDto> addEmployee(@Valid @RequestBody RequestDto request) {
        ResponseDto created = employeeService.addEmployee(request);
        URI location = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/employees/get/{employeeId}")
                .buildAndExpand(created.employeeId())
                .toUri();
        return ResponseEntity.created(location).body(created);
    }

    @PutMapping("/update/{employeeId}")
    public ResponseEntity<ResponseDto> updateEmployee(
            @PathVariable Integer employeeId,
            @Valid @RequestBody RequestDto request) {
        return ResponseEntity.ok(employeeService.updateEmployee(employeeId, request));
    }

    @DeleteMapping("/delete/{employeeId}")
    public ResponseEntity<Void> deleteEmployee(@PathVariable Integer employeeId) {
        employeeService.deleteEmployee(employeeId);
        return ResponseEntity.noContent().build();
    }
}
