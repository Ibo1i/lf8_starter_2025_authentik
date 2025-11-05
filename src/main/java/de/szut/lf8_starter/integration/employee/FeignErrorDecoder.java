package de.szut.lf8_starter.integration.employee;

import de.szut.lf8_starter.exceptionHandling.EmployeeNotFoundException;
import de.szut.lf8_starter.exceptionHandling.EmployeeServiceTimeoutException;
import de.szut.lf8_starter.exceptionHandling.EmployeeServiceUnavailableException;
import feign.Response;
import feign.codec.ErrorDecoder;
import lombok.extern.slf4j.Slf4j;


/**
 * Custom Error Decoder für Feign Client
 * Mappt HTTP-Fehler vom Employee-Service auf spezifische Exceptions
 */
@Slf4j
public class FeignErrorDecoder implements ErrorDecoder {

    private final ErrorDecoder defaultDecoder = new Default();

    @Override
    public Exception decode(String methodKey, Response response) {
        log.error("Employee-Service Error - Method: {}, Status: {}, Reason: {}",
                  methodKey, response.status(), response.reason());

        return switch (response.status()) {
            case 404 -> {
                // Mitarbeiter nicht gefunden
                String employeeId = extractEmployeeIdFromUrl(response.request().url());
                yield new EmployeeNotFoundException(parseEmployeeId(employeeId));
            }
            case 500, 502, 503 ->
                // Service-Fehler
                    new EmployeeServiceUnavailableException(response.status());
            case 504 ->
                // Gateway Timeout
                    new EmployeeServiceTimeoutException();
            default -> defaultDecoder.decode(methodKey, response);
        };
    }

    /**
     * Extrahiert die Employee-ID aus der URL
     */
    private String extractEmployeeIdFromUrl(String url) {
        if (url.contains("/employees/")) {
            String[] parts = url.split("/employees/");
            if (parts.length > 1) {
                String idPart = parts[1];
                // Entferne Query-Parameter oder weitere Pfad-Teile
                if (idPart.contains("/")) {
                    idPart = idPart.substring(0, idPart.indexOf("/"));
                }
                if (idPart.contains("?")) {
                    idPart = idPart.substring(0, idPart.indexOf("?"));
                }
                return idPart;
            }
        }
        return "unknown";
    }

    /**
     * Konvertiert String-ID (z.B. "E-12345") zu Long (12345)
     * Wenn nicht parsbar, verwende 0
     */
    private Long parseEmployeeId(String employeeId) {
        try {
            // Entferne "E-" Präfix falls vorhanden
            if (employeeId.startsWith("E-")) {
                employeeId = employeeId.substring(2);
            }
            return Long.parseLong(employeeId);
        } catch (NumberFormatException e) {
            log.warn("Could not parse employee ID: {}", employeeId);
            return 0L;
        }
    }
}

