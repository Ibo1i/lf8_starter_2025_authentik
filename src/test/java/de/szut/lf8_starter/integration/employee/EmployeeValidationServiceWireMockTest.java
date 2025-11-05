package de.szut.lf8_starter.integration.employee;

import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import de.szut.lf8_starter.exceptionHandling.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration Tests für EmployeeValidationService mit WireMock
 * Testet die Integration mit dem Employee-Service und alle Fehlerszenarien
 */
@SpringBootTest
@WireMockTest(httpPort = 8081)
@TestPropertySource(properties = {
    "employee.service.url=http://localhost:8081",
    "resilience4j.circuitbreaker.instances.employeeService.sliding-window-size=5",
    "resilience4j.circuitbreaker.instances.employeeService.minimum-number-of-calls=3",
    "resilience4j.timelimiter.instances.employeeService.timeout-duration=2s"
})
class EmployeeValidationServiceWireMockTest {

    @Autowired
    private EmployeeValidationService employeeValidationService;

    @BeforeEach
    void setUp() {
        WireMock.reset();
    }

    @Test
    void validateEmployee_Success() {
        // Arrange
        Long employeeId = 12345L;
        stubFor(get(urlEqualTo("/employees/12345"))
            .willReturn(aResponse()
                .withStatus(200)
                .withHeader("Content-Type", "application/json")
                .withBody("""
                    {
                        "employeeId": 12345,
                        "firstName": "Max",
                        "lastName": "Mustermann",
                        "email": "max.mustermann@hitec.de",
                        "status": "ACTIVE"
                    }
                    """)));

        // Act
        boolean result = employeeValidationService.validateEmployee(employeeId);

        // Assert
        assertTrue(result);
        verify(1, getRequestedFor(urlEqualTo("/employees/12345")));
    }

    @Test
    void validateEmployee_NotFound() {
        // Arrange
        Long employeeId = 99999L;
        stubFor(get(urlEqualTo("/employees/99999"))
            .willReturn(aResponse()
                .withStatus(404)));

        // Act & Assert
        assertThrows(EmployeeNotFoundException.class, () -> employeeValidationService.validateEmployee(employeeId));
    }

    @Test
    void validateEmployee_InactiveStatus() {
        // Arrange
        Long employeeId = 12345L;
        stubFor(get(urlEqualTo("/employees/12345"))
            .willReturn(aResponse()
                .withStatus(200)
                .withHeader("Content-Type", "application/json")
                .withBody("""
                    {
                        "employeeId": 12345,
                        "firstName": "Max",
                        "lastName": "Mustermann",
                        "email": "max.mustermann@hitec.de",
                        "status": "INACTIVE"
                    }
                    """)));

        // Act & Assert
        assertThrows(EmployeeNotFoundException.class, () -> employeeValidationService.validateEmployee(employeeId));
    }

    @Test
    void validateQualification_Success() {
        // Arrange
        Long employeeId = 12345L;
        String qualification = "Java Senior Developer";
        stubFor(get(urlEqualTo("/employees/12345/qualifications"))
            .willReturn(aResponse()
                .withStatus(200)
                .withHeader("Content-Type", "application/json")
                .withBody("""
                    {
                        "employeeId": 12345,
                        "qualifications": [
                            {
                                "name": "Java Senior Developer",
                                "level": "SENIOR",
                                "validUntil": "2026-12-31"
                            }
                        ]
                    }
                    """)));

        // Act
        boolean result = employeeValidationService.validateQualification(employeeId, qualification);

        // Assert
        assertTrue(result);
        verify(1, getRequestedFor(urlEqualTo("/employees/12345/qualifications")));
    }

    @Test
    void validateQualification_NotFound() {
        // Arrange
        Long employeeId = 12345L;
        String qualification = "Python Developer";
        stubFor(get(urlEqualTo("/employees/12345/qualifications"))
            .willReturn(aResponse()
                .withStatus(200)
                .withHeader("Content-Type", "application/json")
                .withBody("""
                    {
                        "employeeId": 12345,
                        "qualifications": [
                            {
                                "name": "Java Senior Developer",
                                "level": "SENIOR",
                                "validUntil": "2026-12-31"
                            }
                        ]
                    }
                    """)));

        // Act & Assert
        assertThrows(EmployeeQualificationException.class, () -> employeeValidationService.validateQualification(employeeId, qualification));
    }

    @Test
    void validateQualification_Expired() {
        // Arrange
        Long employeeId = 12345L;
        String qualification = "Java Senior Developer";
        stubFor(get(urlEqualTo("/employees/12345/qualifications"))
            .willReturn(aResponse()
                .withStatus(200)
                .withHeader("Content-Type", "application/json")
                .withBody("""
                    {
                        "employeeId": 12345,
                        "qualifications": [
                            {
                                "name": "Java Senior Developer",
                                "level": "SENIOR",
                                "validUntil": "2020-12-31"
                            }
                        ]
                    }
                    """)));

        // Act & Assert
        assertThrows(QualificationExpiredException.class, () -> employeeValidationService.validateQualification(employeeId, qualification));
    }

    @Test
    void validateEmployee_ServiceUnavailable_500() {
        // Arrange
        Long employeeId = 12345L;
        stubFor(get(urlEqualTo("/employees/12345"))
            .willReturn(aResponse()
                .withStatus(500)));

        // Act & Assert
        assertThrows(EmployeeServiceUnavailableException.class, () -> employeeValidationService.validateEmployee(employeeId));
    }

    @Test
    void validateEmployee_Timeout() {
        // Arrange
        Long employeeId = 12345L;
        stubFor(get(urlEqualTo("/employees/12345"))
            .willReturn(aResponse()
                .withStatus(200)
                .withFixedDelay(5000) // 5 Sekunden Delay (über Timeout von 2s)
                .withHeader("Content-Type", "application/json")
                .withBody("{}")));

        // Act & Assert
        assertThrows(Exception.class, () -> employeeValidationService.validateEmployee(employeeId));
    }

    @Test
    void getEmployeeName_Success() {
        // Arrange
        Long employeeId = 12345L;
        stubFor(get(urlEqualTo("/employees/12345"))
            .willReturn(aResponse()
                .withStatus(200)
                .withHeader("Content-Type", "application/json")
                .withBody("""
                    {
                        "employeeId": 12345,
                        "firstName": "Max",
                        "lastName": "Mustermann",
                        "email": "max.mustermann@hitec.de",
                        "status": "ACTIVE"
                    }
                    """)));

        // Act
        String name = employeeValidationService.getEmployeeName(employeeId);

        // Assert
        assertEquals("Max Mustermann", name);
    }

    @Test
    void getEmployeeName_Fallback() {
        // Arrange
        Long employeeId = 12345L;
        stubFor(get(urlEqualTo("/employees/12345"))
            .willReturn(aResponse()
                .withStatus(500)));

        // Act
        String name = employeeValidationService.getEmployeeName(employeeId);

        // Assert
        assertEquals("Mitarbeiter 12345", name);
    }
}

