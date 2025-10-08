package de.szut.lf8_starter.project.service;

import org.springframework.stereotype.Service;

@Service
public class CustomerService {

    // Dummy-Implementierung für zukünftigen Kunden-Service
    public boolean customerExists(Long customerId) {
        // TODO: Implementierung wenn Kunden-Service verfügbar ist
        // Für jetzt nehmen wir an, dass alle Kunden-IDs gültig sind
        return customerId != null && customerId > 0;
    }
}
