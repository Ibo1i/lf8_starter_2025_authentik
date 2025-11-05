package de.szut.lf8_starter.project;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class ProjectRepositoryTest {

    @Autowired
    private ProjectRepository repository;

    @Test
    void shouldSaveAndLoadProject() {
        // Given - Create project
        ProjectEntity project = new ProjectEntity(
                "Test Webshop",
                1L,
                1L,
                "Max Mustermann",
                "E-Commerce Projekt",
                LocalDate.now(),
                LocalDate.now().plusDays(30)
        );

        // When - Save
        ProjectEntity saved = repository.save(project);

        // Then - Check
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getDesignation()).isEqualTo("Test Webshop");

        // Reload and check again
        Optional<ProjectEntity> loaded = repository.findById(saved.getId());
        assertThat(loaded).isPresent();
        assertThat(loaded.get().getDesignation()).isEqualTo("Test Webshop");
    }

    @Test
    void findProjectsByCustomerId() {
        // Given - Several projects for various clients
        ProjectEntity project1 = erstelleTestProjekt("Projekt 1", 1L);
        ProjectEntity project2 = erstelleTestProjekt("Projekt 2", 1L);
        ProjectEntity project3 = erstelleTestProjekt("Projekt 3", 2L);

        repository.save(project1);
        repository.save(project2);
        repository.save(project3);

        // When - Suche nach Kunde 1
        List<ProjectEntity> projekte = repository.findByCustomerId(1L);

        // Then - Nur 2 Projekte für Kunde 1
        assertThat(projekte).hasSize(2);
        assertThat(projekte)
                .extracting(ProjectEntity::getDesignation)
                .containsExactlyInAnyOrder("Projekt 1", "Projekt 2");
    }

    @Test
    void FindProjectsByPersonResponsible() {
        // Given
        ProjectEntity projekt1 = erstelleTestProjektMitVerantwortlichem("Projekt A", 10L);
        ProjectEntity projekt2 = erstelleTestProjektMitVerantwortlichem("Projekt B", 10L);
        ProjectEntity projekt3 = erstelleTestProjektMitVerantwortlichem("Projekt C", 20L);

        repository.save(projekt1);
        repository.save(projekt2);
        repository.save(projekt3);

        // When
        List<ProjectEntity> projekte = repository.findByResponsibleEmployeeId(10L);

        // Then
        assertThat(projekte).hasSize(2);
        assertThat(projekte)
                .allMatch(p -> p.getResponsibleEmployeeId().equals(10L));
    }

    @Test
    void FindProjectByName() {
        // Given
        ProjectEntity project = erstelleTestProjekt("Eindeutiger Name", 1L);
        repository.save(project);

        // When
        Optional<ProjectEntity> found = repository.findByDesignation("Eindeutiger Name");

        // Then
        assertThat(found).isPresent();
        assertThat(found.get().getDesignation()).isEqualTo("Eindeutiger Name");
    }

    @Test
    void sollLeeresOptionalZurueckgebenWennBezeichnungNichtExistiert() {
        // When
        Optional<ProjectEntity> gefunden = repository.findByDesignation("Nicht Existierend");

        // Then
        assertThat(gefunden).isEmpty();
    }

    @Test
    void sollPruefenObBezeichnungExistiert() {
        // Given
        ProjectEntity projekt = erstelleTestProjekt("Existierendes Projekt", 1L);
        repository.save(projekt);

        // When & Then
        assertThat(repository.existsByDesignation("Existierendes Projekt")).isTrue();
        assertThat(repository.existsByDesignation("Nicht Existierend")).isFalse();
    }

    @Test
    void sollProjekteNachKundeSortiertNachStartdatumFinden() {
        // Given - Projekte mit verschiedenen Startdaten
        ProjectEntity projekt1 = erstelleTestProjektMitDatum("Projekt Z", LocalDate.of(2025, 12, 1));
        ProjectEntity projekt2 = erstelleTestProjektMitDatum("Projekt A", LocalDate.of(2025, 11, 1));
        ProjectEntity projekt3 = erstelleTestProjektMitDatum("Projekt M", LocalDate.of(2025, 10, 1));

        repository.save(projekt1);
        repository.save(projekt2);
        repository.save(projekt3);

        // When - Sortiert nach Startdatum aufsteigend
        List<ProjectEntity> projekte = repository.findByCustomerIdOrderByStartDateAsc(1L);

        // Then - Reihenfolge prüfen
        assertThat(projekte).hasSize(3);
        assertThat(projekte.get(0).getDesignation()).isEqualTo("Projekt M");
        assertThat(projekte.get(1).getDesignation()).isEqualTo("Projekt A");
        assertThat(projekte.get(2).getDesignation()).isEqualTo("Projekt Z");
    }

    @Test
    void sollAlleProjekteZaehlen() {
        // Given
        repository.save(erstelleTestProjekt("Projekt 1", 1L));
        repository.save(erstelleTestProjekt("Projekt 2", 1L));
        repository.save(erstelleTestProjekt("Projekt 3", 2L));

        // When
        long anzahl = repository.count();

        // Then
        assertThat(anzahl).isEqualTo(3);
    }

    @Test
    void sollProjektLoeschen() {
        // Given
        ProjectEntity projekt = erstelleTestProjekt("Zu Löschen", 1L);
        ProjectEntity gespeichert = repository.save(projekt);

        // When
        repository.deleteById(gespeichert.getId());

        // Then
        assertThat(repository.findById(gespeichert.getId())).isEmpty();
    }

    // Helper-Methoden
    private ProjectEntity erstelleTestProjekt(String bezeichnung, Long kundenId) {
        return new ProjectEntity(
                bezeichnung,
                1L,
                kundenId,
                "Max Mustermann",
                "Test Kommentar",
                LocalDate.now(),
                LocalDate.now().plusDays(30)
        );
    }

    private ProjectEntity erstelleTestProjektMitVerantwortlichem(String bezeichnung, Long verantwortlicherMitarbeiterId) {
        return new ProjectEntity(
                bezeichnung,
                verantwortlicherMitarbeiterId,
                1L,
                "Max Mustermann",
                "Test",
                LocalDate.now(),
                LocalDate.now().plusDays(30)
        );
    }

    private ProjectEntity erstelleTestProjektMitDatum(String bezeichnung, LocalDate startDatum) {
        return new ProjectEntity(
                bezeichnung,
                1L,
                1L,
                "Max Mustermann",
                "Test",
                startDatum,
                startDatum.plusDays(30)
        );
    }
}