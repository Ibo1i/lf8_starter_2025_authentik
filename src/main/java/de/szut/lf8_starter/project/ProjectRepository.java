package de.szut.lf8_starter.project;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface ProjectRepository extends JpaRepository<ProjectEntity, Long> {

    List<ProjectEntity> findByResponsibleEmployeeId(Long employeeId);

    @Query("SELECT p FROM ProjectEntity p JOIN p.employeeIds e WHERE e = :employeeId")
    List<ProjectEntity> findProjectsByEmployeeId(@Param("employeeId") Long employeeId);

    @Query("SELECT p FROM ProjectEntity p WHERE p.startDate <= :endDate AND p.plannedEndDate >= :startDate")
    List<ProjectEntity> findProjectsInTimeRange(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
}
