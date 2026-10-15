package com.kloudspot.employee_management_system.service;

import com.kloudspot.employee_management_system.dto.PageDto;
import com.kloudspot.employee_management_system.dto.RequestDto;
import com.kloudspot.employee_management_system.dto.ResponseDto;
import com.kloudspot.employee_management_system.entity.Employee;
import com.kloudspot.employee_management_system.entity.EmployeeStatus;
import com.kloudspot.employee_management_system.exception.DuplicateEmailException;
import com.kloudspot.employee_management_system.exception.ResourceNotFoundException;
import com.kloudspot.employee_management_system.repository.EmployeeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmployeeServiceTest {

    @Mock
    private EmployeeRepository employeeRepository;

    @InjectMocks
    private EmployeeService employeeService;

    private RequestDto request;
    private Employee employee;

    @BeforeEach
    void setUp() {
        request = new RequestDto("Urvashi Jadhav", "urvashi@example.com", "9876543210",
                "Engineering", "Software Engineer", LocalDate.of(2026, 10, 1), 50000, EmployeeStatus.Active);

        employee = new Employee();
        employee.setEmployeeId(1);
        employee.setFullName("Urvashi Jadhav");
        employee.setEmail("urvashi@example.com");
        employee.setPhoneNo("9876543210");
        employee.setDepartment("Engineering");
        employee.setDesignation("Software Engineer");
        employee.setDateOfJoining(LocalDate.of(2026, 10, 1));
        employee.setSalary(50000);
        employee.setStatus(EmployeeStatus.Active);
    }

    // ---------- add ----------

    @Test
    void addEmployee_success() {
        when(employeeRepository.existsByEmail(request.email())).thenReturn(false);
        when(employeeRepository.save(any(Employee.class))).thenReturn(employee);

        ResponseDto result = employeeService.addEmployee(request);

        assertEquals(1, result.employeeId());
        assertEquals("urvashi@example.com", result.email());
        assertEquals(EmployeeStatus.Active, result.status());
        verify(employeeRepository).save(any(Employee.class));
    }

    @Test
    void addEmployee_duplicateEmail_throwsAndDoesNotSave() {
        when(employeeRepository.existsByEmail(request.email())).thenReturn(true);

        assertThrows(DuplicateEmailException.class, () -> employeeService.addEmployee(request));
        verify(employeeRepository, never()).save(any());
    }

    // ---------- get by id ----------

    @Test
    void getEmployeeById_found() {
        when(employeeRepository.findById(1)).thenReturn(Optional.of(employee));

        ResponseDto result = employeeService.getEmployeeById(1);

        assertEquals("Urvashi Jadhav", result.fullName());
        assertEquals("Engineering", result.department());
    }

    @Test
    void getEmployeeById_notFound_throws() {
        when(employeeRepository.findById(99)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> employeeService.getEmployeeById(99));
    }

    // ---------- list / search / filter / pagination ----------

    @Test
    void getAllEmployees_blankFiltersBecomeNull() {
        Page<Employee> page = new PageImpl<>(List.of(employee), PageRequest.of(0, 10), 1);
        when(employeeRepository.findWithFilters(any(), any(), any(), any(Pageable.class))).thenReturn(page);

        PageDto<ResponseDto> result = employeeService.getAllEmployees("  ", "", null, 0, 10);

        verify(employeeRepository).findWithFilters(isNull(), isNull(), isNull(), any(Pageable.class));
        assertEquals(1, result.content().size());
        assertEquals(1, result.totalElements());
        assertEquals(1, result.totalPages());
    }

    @Test
    void getAllEmployees_passesTrimmedFilters() {
        Page<Employee> page = new PageImpl<>(List.of(employee), PageRequest.of(0, 5), 1);
        when(employeeRepository.findWithFilters(any(), any(), any(), any(Pageable.class))).thenReturn(page);

        employeeService.getAllEmployees(" urv ", "Engineering", EmployeeStatus.Active, 0, 5);

        verify(employeeRepository).findWithFilters(eq("urv"), eq("Engineering"),
                eq(EmployeeStatus.Active), any(Pageable.class));
    }

    @Test
    void getAllEmployees_capsPageSizeAndFixesNegativePage() {
        Page<Employee> page = new PageImpl<>(List.of(), PageRequest.of(0, 100), 0);
        when(employeeRepository.findWithFilters(any(), any(), any(), any(Pageable.class))).thenReturn(page);

        employeeService.getAllEmployees(null, null, null, -5, 5000);

        ArgumentCaptor<Pageable> captor = ArgumentCaptor.forClass(Pageable.class);
        verify(employeeRepository).findWithFilters(any(), any(), any(), captor.capture());
        assertEquals(0, captor.getValue().getPageNumber());
        assertEquals(100, captor.getValue().getPageSize());
    }

    // ---------- update ----------

    @Test
    void updateEmployee_success() {
        RequestDto updated = new RequestDto("Urvashi J", "urvashi@example.com", "9876543210",
                "Engineering", "Senior Engineer", LocalDate.of(2026, 10, 1), 70000, EmployeeStatus.Inactive);
        when(employeeRepository.findById(1)).thenReturn(Optional.of(employee));
        when(employeeRepository.existsByEmailAndEmployeeIdNot(updated.email(), 1)).thenReturn(false);
        when(employeeRepository.save(any(Employee.class))).thenAnswer(inv -> inv.getArgument(0));

        ResponseDto result = employeeService.updateEmployee(1, updated);

        assertEquals("Urvashi J", result.fullName());
        assertEquals("Senior Engineer", result.designation());
        assertEquals(70000, result.salary());
        assertEquals(EmployeeStatus.Inactive, result.status());
    }

    @Test
    void updateEmployee_notFound_throws() {
        when(employeeRepository.findById(99)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> employeeService.updateEmployee(99, request));
        verify(employeeRepository, never()).save(any());
    }

    @Test
    void updateEmployee_emailUsedByAnother_throwsDuplicate() {
        when(employeeRepository.findById(1)).thenReturn(Optional.of(employee));
        when(employeeRepository.existsByEmailAndEmployeeIdNot(request.email(), 1)).thenReturn(true);

        assertThrows(DuplicateEmailException.class, () -> employeeService.updateEmployee(1, request));
        verify(employeeRepository, never()).save(any());
    }

    // ---------- delete ----------

    @Test
    void deleteEmployee_success() {
        when(employeeRepository.findById(1)).thenReturn(Optional.of(employee));

        employeeService.deleteEmployee(1);

        verify(employeeRepository).delete(employee);
    }

    @Test
    void deleteEmployee_notFound_throws() {
        when(employeeRepository.findById(99)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> employeeService.deleteEmployee(99));
        verify(employeeRepository, never()).delete(any());
    }
}
