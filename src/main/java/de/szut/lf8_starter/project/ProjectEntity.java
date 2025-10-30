package de.szut.lf8_starter.project;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "projects")
public class ProjectEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String designation;

    @Column(name = "responsible_employee_id", nullable = false)
    private Long responsibleEmployeeId;

    @Column(name = "customer_id", nullable = false)
    private Long customerId;

    @Column(name = "customer_contact_person")
    private String customerContactPerson;

    @Column(columnDefinition = "TEXT")
    private String comment;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "planned_end_date", nullable = false)
    private LocalDate plannedEndDate;

    @Column(name = "actual_end_date")
    private LocalDate actualEndDate;

    @ElementCollection
    @CollectionTable(
        name = "project_employee_assignments",
        joinColumns = @JoinColumn(name = "project_id"),
        uniqueConstraints = @UniqueConstraint(columnNames = {"project_id", "employee_id"})
    )
    @Column(name = "employee_id")
    private Set<Long> employeeIds = new HashSet<>();

    @ElementCollection
    @CollectionTable(
        name = "project_employee_qualifications",
        joinColumns = @JoinColumn(name = "project_id"),
        uniqueConstraints = @UniqueConstraint(columnNames = {"project_id", "employee_id"})
    )
    @MapKeyColumn(name = "employee_id")
    @Column(name = "qualification")
    private Map<Long, String> employeeQualifications = new HashMap<>();

    // New: store the date when the employee was assigned to the project
    @ElementCollection
    @CollectionTable(
        name = "project_employee_assigned_dates",
        joinColumns = @JoinColumn(name = "project_id"),
        uniqueConstraints = @UniqueConstraint(columnNames = {"project_id", "employee_id"})
    )
    @MapKeyColumn(name = "employee_id")
    @Column(name = "assigned_date")
    private Map<Long, LocalDate> employeeAssignedDates = new HashMap<>();

    public ProjectEntity(String designation, Long responsibleEmployeeId, Long customerId,
                        String customerContactPerson, String comment, LocalDate startDate,
                        LocalDate plannedEndDate) {
        this.designation = designation;
        this.responsibleEmployeeId = responsibleEmployeeId;
        this.customerId = customerId;
        this.customerContactPerson = customerContactPerson;
        this.comment = comment;
        this.startDate = startDate;
        this.plannedEndDate = plannedEndDate;
    }
}
