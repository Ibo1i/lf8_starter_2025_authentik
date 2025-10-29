package de.szut.lf8_starter.exceptionHandling;

public class ProjectNotFoundException extends RuntimeException {
    public ProjectNotFoundException(Long id) {
        super("Projekt mit ID " + id + " nicht gefunden");
    }
}