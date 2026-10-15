package com.kloudspot.employee_management_system.service;

import com.kloudspot.employee_management_system.dto.RequestDto;
import com.kloudspot.employee_management_system.dto.ResponseDto;
import com.kloudspot.employee_management_system.dto.PageDto;
import com.kloudspot.employee_management_system.entity.Employee;
import com.kloudspot.employee_management_system.entity.EmployeeStatus;
import com.kloudspot.employee_management_system.exception.DuplicateEmailException;
import com.kloudspot.employee_management_system.exception.ResourceNotFoundException;
import com.kloudspot.employee_management_system.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class EmployeeService {

    private static final int MAX_PAGE_SIZE = 100;

    private final EmployeeRepository employeeRepository;

    @Transactional
    public ResponseDto addEmployee(RequestDto request) {
        if (employeeRepository.existsByEmail(request.email())) {
            throw new DuplicateEmailException("Email already exists: " + request.email());
        }
        Employee saved = employeeRepository.save(mapToEntity(new Employee(), request));
        return mapToResponse(saved);
    }

    @Transactional(readOnly = true)
    public ResponseDto getEmployeeById(Integer employeeId) {
        return mapToResponse(findEmployeeOrThrow(employeeId));
    }

    @Transactional(readOnly = true)
    public PageDto<ResponseDto> getAllEmployees(String search, String department,
                                                          EmployeeStatus status,
                                                          int page, int size) {
        int safePage = Math.max(page, 0);
        int safeSize = Math.min(Math.max(size, 1), MAX_PAGE_SIZE);
        Pageable pageable = PageRequest.of(safePage, safeSize, Sort.by("employeeId").descending());

        Page<Employee> result = employeeRepository.findWithFilters(
                blankToNull(search), blankToNull(department), status, pageable);

        return new PageDto<>(
                result.getContent().stream().map(this::mapToResponse).toList(),
                result.getNumber(),
                result.getSize(),
                result.getTotalElements(),
                result.getTotalPages());
    }

    @Transactional
    public ResponseDto updateEmployee(Integer employeeId, RequestDto request) {
        Employee existing = findEmployeeOrThrow(employeeId);
        if (employeeRepository.existsByEmailAndEmployeeIdNot(request.email(), employeeId)) {
            throw new DuplicateEmailException("Email already exists: " + request.email());
        }
        return mapToResponse(employeeRepository.save(mapToEntity(existing, request)));
    }

    @Transactional
    public void deleteEmployee(Integer employeeId) {
        employeeRepository.delete(findEmployeeOrThrow(employeeId));
    }

    // ---------- helper methods ----------

    private Employee findEmployeeOrThrow(Integer employeeId) {
        return employeeRepository.findById(employeeId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Employee not found with id: " + employeeId));
    }

    private String blankToNull(String value) {
        return (value == null || value.isBlank()) ? null : value.trim();
    }

    private Employee mapToEntity(Employee employee, RequestDto request) {
        employee.setFullName(request.fullName());
        employee.setEmail(request.email());
        employee.setPhoneNo(request.phoneNo());
        employee.setDepartment(request.department());
        employee.setDesignation(request.designation());
        employee.setDateOfJoining(request.dateOfJoining());
        employee.setSalary(request.salary());
        employee.setStatus(request.status());
        return employee;
    }

    private ResponseDto mapToResponse(Employee employee) {
        return new ResponseDto(
                employee.getEmployeeId(),
                employee.getFullName(),
                employee.getEmail(),
                employee.getPhoneNo(),
                employee.getDepartment(),
                employee.getDesignation(),
                employee.getDateOfJoining(),
                employee.getSalary(),
                employee.getStatus());
    }
}