package de.szut.lf8_starter.repository;

import de.szut.lf8_starter.project.ProjectEntity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class ProjektRepositoryTest {

    @Autowired
    private ProjektRepository repository;

    @Test
    void sollProjektSpeichernUndLaden() {
        // Given - Projekt erstellen
        ProjectEntity projekt = new ProjectEntity(
                "Test Webshop",
                1L,
                1L,
                "Max Mustermann",
                "E-Commerce Projekt",
                LocalDate.now(),
                LocalDate.now().plusDays(30)
        );

        // When - Speichern
        ProjectEntity gespeichert = repository.save(projekt);

        // Then - Prüfen
        assertThat(gespeichert.getId()).isNotNull();
        assertThat(gespeichert.getDesignation()).isEqualTo("Test Webshop");

        // Nochmal laden und prüfen
        Optional<ProjectEntity> geladen = repository.findById(gespeichert.getId());
        assertThat(geladen).isPresent();
        assertThat(geladen.get().getDesignation()).isEqualTo("Test Webshop");
    }

    @Test
    void sollProjekteNachKundenIdFinden() {
        // Given - Mehrere Projekte für verschiedene Kunden
        ProjectEntity projekt1 = erstelleTestProjekt("Projekt 1", 1L);
        ProjectEntity projekt2 = erstelleTestProjekt("Projekt 2", 1L);
        ProjectEntity projekt3 = erstelleTestProjekt("Projekt 3", 2L);

        repository.save(projekt1);
        repository.save(projekt2);
        repository.save(projekt3);

        // When - Suche nach Kunde 1
        List<ProjectEntity> projekte = repository.findByCustomerId(1L);

        // Then - Nur 2 Projekte für Kunde 1
        assertThat(projekte).hasSize(2);
        assertThat(projekte)
                .extracting(ProjectEntity::getDesignation)
                .containsExactlyInAnyOrder("Projekt 1", "Projekt 2");
    }

    @Test
    void sollProjekteNachVerantwortlichemFinden() {
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
    void sollProjektNachBezeichnungFinden() {
        // Given
        ProjectEntity projekt = erstelleTestProjekt("Eindeutiger Name", 1L);
        repository.save(projekt);

        // When
        Optional<ProjectEntity> gefunden = repository.findByDesignation("Eindeutiger Name");

        // Then
        assertThat(gefunden).isPresent();
        assertThat(gefunden.get().getDesignation()).isEqualTo("Eindeutiger Name");
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
        ProjectEntity projekt1 = erstelleTestProjektMitDatum("Projekt Z", 1L, LocalDate.of(2025, 12, 1));
        ProjectEntity projekt2 = erstelleTestProjektMitDatum("Projekt A", 1L, LocalDate.of(2025, 11, 1));
        ProjectEntity projekt3 = erstelleTestProjektMitDatum("Projekt M", 1L, LocalDate.of(2025, 10, 1));

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

    private ProjectEntity erstelleTestProjektMitDatum(String bezeichnung, Long kundenId, LocalDate startDatum) {
        return new ProjectEntity(
                bezeichnung,
                1L,
                kundenId,
                "Max Mustermann",
                "Test",
                startDatum,
                startDatum.plusDays(30)
        );
    }
}