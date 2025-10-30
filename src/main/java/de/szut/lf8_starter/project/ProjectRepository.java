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

    // Suche nach Kunden-ID
    List<ProjectEntity> findByCustomerId(Long customerId);

    // Suche nach Verantwortlichem (Employee-ID)
    List<ProjectEntity> findByResponsibleEmployeeId(Long employeeId);

    // Suche nach Bezeichnung
    Optional<ProjectEntity> findByDesignation(String designation);

    // Prüfe ob Bezeichnung existiert
    boolean existsByDesignation(String designation);

    // Suche nach Kunde, sortiert nach Startdatum
    List<ProjectEntity> findByCustomerIdOrderByStartDateAsc(Long customerId);

    @Query("SELECT p FROM ProjectEntity p JOIN p.employeeIds e WHERE e = :employeeId")
    List<ProjectEntity> findProjectsByEmployeeId(@Param("employeeId") Long employeeId);

    @Query("SELECT p FROM ProjectEntity p WHERE p.startDate <= :endDate AND p.plannedEndDate >= :startDate")
    List<ProjectEntity> findProjectsInTimeRange(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
}
