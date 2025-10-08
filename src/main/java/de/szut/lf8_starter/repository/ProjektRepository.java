package de.szut.lf8_starter.repository;

import de.szut.lf8_starter.project.ProjectEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProjektRepository extends JpaRepository<ProjectEntity, Long> {

    // Suche nach Kunden-ID
    List<ProjectEntity> findByCustomerId(Long customerId);

    // Suche nach Verantwortlichem (Employee-ID)
    List<ProjectEntity> findByResponsibleEmployeeId(Long responsibleEmployeeId);

    // Suche nach Bezeichnung
    Optional<ProjectEntity> findByDesignation(String designation);

    // Prüfe ob Bezeichnung existiert
    boolean existsByDesignation(String designation);

    // Suche nach Kunde, sortiert nach Startdatum
    List<ProjectEntity> findByCustomerIdOrderByStartDateAsc(Long customerId);
}