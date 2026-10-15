package com.kloudspot.employee_management_system.repository;

import com.kloudspot.employee_management_system.entity.Employee;
import com.kloudspot.employee_management_system.entity.EmployeeStatus;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;




@Repository
public interface EmployeeRepository extends JpaRepository<Employee,Integer> {

    boolean existsByEmail(String email);
    boolean existsByEmailAndEmployeeIdNot(String email, int id);

    @Query("""
        SELECT e FROM Employee e
        WHERE (:search IS NULL
               OR LOWER(e.fullName) LIKE LOWER(CONCAT('%', :search, '%'))
               OR LOWER(e.email)    LIKE LOWER(CONCAT('%', :search, '%')))
          AND (:department IS NULL OR e.department = :department)
          AND (:status IS NULL OR e.status = :status)
        """)

    Page<Employee> findWithFilters(@Param("search") String search,
                                   @Param("department") String department,
                                   @Param("status") EmployeeStatus status,
                                   Pageable pageable);
}
