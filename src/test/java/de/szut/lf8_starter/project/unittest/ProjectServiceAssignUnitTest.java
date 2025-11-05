package de.szut.lf8_starter.project.unittest;

import de.szut.lf8_starter.exceptionHandling.DuplicateAssignmentException;
import de.szut.lf8_starter.exceptionHandling.EmployeeNotFoundException;
import de.szut.lf8_starter.exceptionHandling.EmployeeQualificationException;
import de.szut.lf8_starter.exceptionHandling.TimeConflictException;
import de.szut.lf8_starter.project.ProjectEntity;
import de.szut.lf8_starter.project.ProjectRepository;
import de.szut.lf8_starter.project.ProjectService;
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
import java.util.Collections;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProjectServiceAssignUnitTest {

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private EmployeeService employeeService;

    @Mock
    private CustomerService customerService;

    @InjectMocks
    private ProjectService projectService;

    private ProjectEntity project;

    @BeforeEach
    void setUp() {
        project = new ProjectEntity();
        project.setId(1L);
        project.setDesignation("TestProjekt");
        project.setCustomerId(10L);
        project.setResponsibleEmployeeId(20L);
        project.setStartDate(LocalDate.of(2025,1,1));
        project.setPlannedEndDate(LocalDate.of(2025,12,31));
    }

    @Test
    @DisplayName("addEmployeeToProject - success")
    void addEmployeeToProject_success() {
        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));
        when(employeeService.employeeExists(100L)).thenReturn(true);
        when(employeeService.employeeHasQualification(100L, "JAVA")).thenReturn(true);
        when(projectRepository.findProjectsInTimeRange(project.getStartDate(), project.getPlannedEndDate())).thenReturn(Collections.emptyList());
        when(projectRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        ProjectEntity result = projectService.addEmployeeToProject(1L, 100L, "JAVA");

        assertThat(result.getEmployeeIds()).contains(100L);
        assertThat(result.getEmployeeQualifications().get(100L)).isEqualTo("JAVA");

        verify(projectRepository).save(result);
    }

    @Test
    @DisplayName("addEmployeeToProject - employee not found")
    void addEmployeeToProject_employeeNotFound() {
        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));
        when(employeeService.employeeExists(200L)).thenReturn(false);

        assertThrows(EmployeeNotFoundException.class, () -> projectService.addEmployeeToProject(1L, 200L, "JAVA"));
    }

    @Test
    @DisplayName("addEmployeeToProject - missing qualification")
    void addEmployeeToProject_missingQualification() {
        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));
        when(employeeService.employeeExists(300L)).thenReturn(true);
        when(employeeService.employeeHasQualification(300L, "PYTHON")).thenReturn(false);

        assertThrows(EmployeeQualificationException.class, () -> projectService.addEmployeeToProject(1L, 300L, "PYTHON"));
    }

    @Test
    @DisplayName("addEmployeeToProject - time conflict")
    void addEmployeeToProject_timeConflict() {
        ProjectEntity other = new ProjectEntity();
        other.setId(2L);
        other.setDesignation("Other");
        other.setStartDate(LocalDate.of(2025,1,1));
        other.setPlannedEndDate(LocalDate.of(2025,12,31));
        other.getEmployeeIds().add(400L);

        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));
        when(employeeService.employeeExists(400L)).thenReturn(true);
        when(employeeService.employeeHasQualification(400L, "DEV")).thenReturn(true);
        when(projectRepository.findProjectsInTimeRange(project.getStartDate(), project.getPlannedEndDate())).thenReturn(Collections.singletonList(other));

        assertThrows(TimeConflictException.class, () -> projectService.addEmployeeToProject(1L, 400L, "DEV"));
    }

    @Test
    @DisplayName("addEmployeeToProject - duplicate assignment")
    void addEmployeeToProject_duplicateAssignment() {
        project.getEmployeeIds().add(500L);
        project.getEmployeeQualifications().put(500L, "Backend Developer");
        project.getEmployeeAssignedDates().put(500L, LocalDate.of(2025,1,15));

        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));

        DuplicateAssignmentException ex = assertThrows(DuplicateAssignmentException.class,
            () -> projectService.addEmployeeToProject(1L, 500L, "Backend Developer"));

        assertThat(ex.getProjectId()).isEqualTo(1L);
        assertThat(ex.getEmployeeId()).isEqualTo(500L);
        assertThat(ex.getAssignedDate()).isEqualTo(LocalDate.of(2025,1,15));
        assertThat(ex.getRole()).isEqualTo("Backend Developer");
    }
}
