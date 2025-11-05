package de.szut.lf8_starter.project;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProjectRepository extends JpaRepository<ProjectEntity, Long> {

    // Find by customer ID
    List<ProjectEntity> findByCustomerId(Long customerId);

    // Find by responsible employee (Employee ID)
    List<ProjectEntity> findByResponsibleEmployeeId(Long employeeId);

    // Find by designation
    Optional<ProjectEntity> findByDesignation(String designation);

    // Check if designation exists
    boolean existsByDesignation(String designation);

    // Find by customer, sorted by start date
    List<ProjectEntity> findByCustomerIdOrderByStartDateAsc(Long customerId);

    @Query("SELECT p FROM ProjectEntity p JOIN p.employeeIds e WHERE e = :employeeId")
    List<ProjectEntity> findProjectsByEmployeeId(@Param("employeeId") Long employeeId);

    @Query("SELECT p FROM ProjectEntity p WHERE p.startDate <= :endDate AND p.plannedEndDate >= :startDate")
    List<ProjectEntity> findProjectsInTimeRange(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
}
