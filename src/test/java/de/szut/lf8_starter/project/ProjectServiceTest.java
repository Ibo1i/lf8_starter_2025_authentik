package de.szut.lf8_starter.project;

import de.szut.lf8_starter.exceptionHandling.ResourceNotFoundException;
import de.szut.lf8_starter.project.service.CustomerService;
import de.szut.lf8_starter.project.service.EmployeeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("ProjectService Tests")
class ProjectServiceTest {

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private EmployeeService employeeService;

    @Mock
    private CustomerService customerService;

    @InjectMocks
    private ProjectService projectService;

    private ProjectEntity testProject;

    @BeforeEach
    void setUp() {
        testProject = new ProjectEntity();
        testProject.setId(1L);
        testProject.setDesignation("Test Projekt");
        testProject.setCustomerId(100L);
        testProject.setResponsibleEmployeeId(50L);
        testProject.setComment("Test Kommentar");
        testProject.setStartDate(LocalDate.of(2025, 1, 1));
        testProject.setPlannedEndDate(LocalDate.of(2025, 12, 31));
    }

    @Test
    @DisplayName("readById - Erfolgreiches Abrufen eines existierenden Projekts")
    void readById_ExistingProject_ReturnsProject() {
        // Given
        Long projectId = 1L;
        when(projectRepository.findById(projectId)).thenReturn(Optional.of(testProject));

        // When
        ProjectEntity result = projectService.readById(projectId);

        // Then
        assertNotNull(result);
        assertEquals(testProject.getId(), result.getId());
        assertEquals(testProject.getDesignation(), result.getDesignation());
        assertEquals(testProject.getCustomerId(), result.getCustomerId());
        assertEquals(testProject.getResponsibleEmployeeId(), result.getResponsibleEmployeeId());
        assertEquals(testProject.getComment(), result.getComment());
        assertEquals(testProject.getStartDate(), result.getStartDate());
        assertEquals(testProject.getPlannedEndDate(), result.getPlannedEndDate());
    }

    @Test
    @DisplayName("readById - Projekt nicht gefunden - wirft ResourceNotFoundException")
    void readById_ProjectNotFound_ThrowsResourceNotFoundException() {
        // Given
        Long nonExistentId = 999L;
        when(projectRepository.findById(nonExistentId)).thenReturn(Optional.empty());

        // When & Then
        ResourceNotFoundException exception = assertThrows(
            ResourceNotFoundException.class,
            () -> projectService.readById(nonExistentId)
        );

        assertEquals("Projekt mit der ID " + nonExistentId + " existiert nicht.", exception.getMessage());
    }

    @Test
    @DisplayName("readById - Negative ID - wirft ResourceNotFoundException")
    void readById_NegativeId_ThrowsResourceNotFoundException() {
        // Given
        Long negativeId = -1L;
        when(projectRepository.findById(negativeId)).thenReturn(Optional.empty());

        // When & Then
        ResourceNotFoundException exception = assertThrows(
            ResourceNotFoundException.class,
            () -> projectService.readById(negativeId)
        );

        assertEquals("Projekt mit der ID " + negativeId + " existiert nicht.", exception.getMessage());
    }

    @Test
    @DisplayName("readById - ID = 0 - wirft ResourceNotFoundException")
    void readById_ZeroId_ThrowsResourceNotFoundException() {
        // Given
        Long zeroId = 0L;
        when(projectRepository.findById(zeroId)).thenReturn(Optional.empty());

        // When & Then
        ResourceNotFoundException exception = assertThrows(
            ResourceNotFoundException.class,
            () -> projectService.readById(zeroId)
        );

        assertEquals("Projekt mit der ID " + zeroId + " existiert nicht.", exception.getMessage());
    }
}
