package de.szut.lf8_starter.project.unittest;

import de.szut.lf8_starter.exceptionHandling.ResourceNotFoundException;
import de.szut.lf8_starter.project.ProjectEntity;
import de.szut.lf8_starter.project.ProjectRepository;
import de.szut.lf8_starter.project.ProjectService;
import de.szut.lf8_starter.project.dto.ProjectEmployeesDto;
import de.szut.lf8_starter.project.service.CustomerService;
import de.szut.lf8_starter.project.service.EmployeeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ProjectService Tests")
class ProjectServiceUnitTest {

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
    @DisplayName("readById - Successfully retrieving an existing project")
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
    @DisplayName("readById - Project not found - throws ResourceNotFoundException")
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
    @DisplayName("readById - Negative ID - throws ResourceNotFoundException")
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
    @DisplayName("readById - ID = 0 - throws ResourceNotFoundException")
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

    @Test
    @DisplayName("deleteById - Successful deletion")
    void deleteById_ExistingProject_Success() {
        // Given
        Long id = 1L;
        when(projectRepository.findById(id)).thenReturn(Optional.of(testProject));
        doNothing().when(projectRepository).delete(testProject);

        // When
        projectService.deleteById(id);

        // Then
        verify(projectRepository, times(1)).delete(testProject);
    }

    @Test
    @DisplayName("deleteById - Project not found - throws ResourceNotFoundException")
    void deleteById_NotFound_ThrowsResourceNotFoundException() {
        Long id = 999L;
        when(projectRepository.findById(id)).thenReturn(Optional.empty());

        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class, () -> projectService.deleteById(id));
        assertEquals("Projekt mit der ID " + id + " existiert nicht.", ex.getMessage());
    }

    @Test
    @DisplayName("deleteById - Conflict in employee assignments - throws ResponseStatusException CONFLICT")
    void deleteById_Conflict_ThrowsResponseStatusException() {
        Long id = 2L;
        ProjectEntity projectWithEmployees = new ProjectEntity();
        projectWithEmployees.setId(id);
        projectWithEmployees.getEmployeeIds().add(10L);

        when(projectRepository.findById(id)).thenReturn(Optional.of(projectWithEmployees));

        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> projectService.deleteById(id));
        assertEquals(HttpStatus.CONFLICT, ex.getStatusCode());
    }

    @Test
    @DisplayName("getProjectEmployees - Successfully retrieving the employees assigned to a project")
    void getProjectEmployees_ExistingProject_ReturnsEmployeesDto() {
        // Given
        Long projectId = 1L;
        testProject.getEmployeeIds().add(10L);
        testProject.getEmployeeQualifications().put(10L, "Developer");

        when(projectRepository.findById(projectId)).thenReturn(Optional.of(testProject));

        // When
        ProjectEmployeesDto dto = projectService.getProjectEmployees(projectId);

        // Then
        assertNotNull(dto);
        assertEquals(projectId, dto.getProjectId());
        assertEquals(testProject.getDesignation(), dto.getDesignation());
        assertNotNull(dto.getEmployees());
        assertEquals(1, dto.getEmployees().size());
        assertEquals(10L, dto.getEmployees().getFirst().getEmployeeId());
        assertEquals("Developer", dto.getEmployees().getFirst().getQualification());
    }

    @Test
    @DisplayName("getProjectEmployees - Project not found - throws ResourceNotFoundException")
    void getProjectEmployees_ProjectNotFound_ThrowsResourceNotFoundException() {
        // Given
        Long nonExistentId = 999L;
        when(projectRepository.findById(nonExistentId)).thenReturn(Optional.empty());

        // When & Then
        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class, () -> projectService.getProjectEmployees(nonExistentId));
        assertEquals("Projekt mit der ID " + nonExistentId + " existiert nicht.", ex.getMessage());
    }
}
