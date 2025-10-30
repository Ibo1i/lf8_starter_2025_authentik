package de.szut.lf8_starter.project.UnitTest;

import de.szut.lf8_starter.exceptionHandling.ResourceNotFoundException;
import de.szut.lf8_starter.project.ProjectEntity;
import de.szut.lf8_starter.project.ProjectRepository;
import de.szut.lf8_starter.project.ProjectService;
import de.szut.lf8_starter.project.service.EmployeeService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ProjectServiceRemoveEmployeeUnitTest {

    @Mock
    private ProjectRepository repository;

    @Mock
    private EmployeeService employeeService;

    @Mock
    private de.szut.lf8_starter.project.service.CustomerService customerService;

    @InjectMocks
    private ProjectService projectService;

    private AutoCloseable mocks;

    @BeforeEach
    void setUp() {
        mocks = MockitoAnnotations.openMocks(this);
    }

    @AfterEach
    void tearDown() throws Exception {
        if (mocks != null) mocks.close();
    }

    @Test
    @DisplayName("removeEmployeeFromProject - Erfolg: Mitarbeiter entfernt")
    void removeEmployee_Success() {
        Long projectId = 1L;
        Long employeeId = 10L;

        ProjectEntity project = new ProjectEntity();
        project.setId(projectId);
        project.setEmployeeIds(new HashSet<>());
        project.getEmployeeIds().add(employeeId);
        project.setEmployeeQualifications(new HashMap<>());
        project.getEmployeeQualifications().put(employeeId, "DEV");

        when(repository.findById(projectId)).thenReturn(Optional.of(project));
        when(repository.save(any(ProjectEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ProjectEntity result = projectService.removeEmployeeFromProject(projectId, employeeId);

        assertNotNull(result);
        assertFalse(result.getEmployeeIds().contains(employeeId));
        assertFalse(result.getEmployeeQualifications().containsKey(employeeId));

        verify(repository).findById(projectId);
        verify(repository).save(project);
    }

    @Test
    @DisplayName("removeEmployeeFromProject - Fehler: Projekt nicht gefunden")
    void removeEmployee_ProjectNotFound() {
        Long projectId = 99L;
        Long employeeId = 10L;

        when(repository.findById(projectId)).thenReturn(Optional.empty());

        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class,
            () -> projectService.removeEmployeeFromProject(projectId, employeeId));

        assertTrue(ex.getMessage().contains("Projekt mit der ID"));
        verify(repository).findById(projectId);
        verify(repository, never()).save(any());
    }

    @Test
    @DisplayName("removeEmployeeFromProject - Fehler: Mitarbeiter nicht im Projekt")
    void removeEmployee_EmployeeNotInProject() {
        Long projectId = 1L;
        Long employeeId = 11L;

        ProjectEntity project = new ProjectEntity();
        project.setId(projectId);
        project.setEmployeeIds(new HashSet<>()); // leer
        project.setEmployeeQualifications(new HashMap<>());

        when(repository.findById(projectId)).thenReturn(Optional.of(project));

        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class,
            () -> projectService.removeEmployeeFromProject(projectId, employeeId));

        assertTrue(ex.getMessage().contains("Mitarbeiter mit der Mitarbeiternummer"));
        verify(repository).findById(projectId);
        verify(repository, never()).save(any());
    }
}
