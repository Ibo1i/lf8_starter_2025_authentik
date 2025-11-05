# HiTec Project Management Service

## 📋 Projektübersicht

Dieses Projekt ist eine **Spring Boot REST API** für die Verwaltung von Projekten bei der Firma HiTec. 

### ✨ Hauptfunktionalitäten

Die Anwendung implementiert folgende zentrale Features:

1. **Projektverwaltung** - CRUD-Operationen für Projekte
2. **Mitarbeiterzuordnung** - Zuordnung von Mitarbeitern zu Projekten mit Validierung
3. **JWT-Authentifizierung** - Sichere API mit Keycloak OAuth2/OIDC
4. **Employee Service Integration** - Externe Mitarbeiterdatenbank mit Circuit Breaker Pattern
5. **Zeitkonflikterkennung** - Automatische Prüfung von überlappenden Projektzeiträumen
6. **Qualifikationsprüfung** - Validierung von Mitarbeiterqualifikationen und Gültigkeitszeiträumen
7. **REST API Dokumentation** - Vollständige OpenAPI/Swagger Dokumentation
8. **Fehlerbehandlung** - Umfassende Exception Handling mit aussagekräftigen Fehlermeldungen

### 🎯 Implementierte Anforderungen

- ✅ **Resilience4j Circuit Breaker** - Ausfallsicherheit bei Employee Service
- ✅ **Spring Security mit JWT** - OAuth2 Resource Server mit Keycloak
- ✅ **PostgreSQL Datenbank** - Persistierung mit Spring Data JPA
- ✅ **OpenFeign Client** - Deklarative HTTP-Kommunikation
- ✅ **Business Validierung** - Zeitkonflikte, Qualifikationsprüfung, Duplikatserkennung
- ✅ **Integration Tests** - Vollständige Test-Coverage mit Testcontainers
- ✅ **WireMock Mock Server** - Simulation der Employee Service API
- ✅ **OpenAPI 3.0 Dokumentation** - Interaktive Swagger UI

---

## 🚀 Schnellstart für Lehrer

### Voraussetzungen

- **Docker Desktop** installiert und gestartet
- **Java 22** (wird durch Gradle Wrapper automatisch verwendet)
- **IntelliJ IDEA** (empfohlen für .http Files)

### 1️⃣ Services starten

Öffnen Sie ein Terminal im Projektverzeichnis und führen Sie aus:

```bash
docker compose up
```

**Dies startet automatisch:**
- ✅ PostgreSQL Database (Port 5432)
- ✅ Keycloak (Port 9090) - Wartezeit ca. 60 Sekunden
- ✅ Employee Service Mock via WireMock (Port 7070)

**Wichtig:** Warten Sie, bis Keycloak vollständig gestartet ist (Status: "healthy").

### 2️⃣ Keycloak konfigurieren

⚠️ **Wichtiger Schritt - ohne diesen funktioniert die Authentifizierung nicht!**

Folgen Sie der detaillierten Anleitung in: **`KEYCLOAK_SETUP_GUIDE.md`**

**Kurzversion:**
1. Öffnen Sie http://localhost:9090
2. Melden Sie sich an: `admin` / `admin`
3. Erstellen Sie Realm: `hitec-realm`
4. Erstellen Sie Client: `project-management-service`
5. Aktivieren Sie "Client authentication" und "Service account roles"
6. Kopieren Sie das **Client Secret** aus dem "Credentials" Tab
7. Tragen Sie das Secret in `src/main/ProjectRequests.http` ein (Zeile 8)

### 3️⃣ Spring Boot Anwendung starten

**Windows:**
```bash
.\gradlew.bat bootRun
```

**Linux/Mac:**
```bash
./gradlew bootRun
```

Die Anwendung startet auf: **http://localhost:8080**

### 4️⃣ API testen

**Empfohlene Methode (IntelliJ):**

Öffnen Sie die Datei: **`src/main/ProjectRequests.http`**

Diese Datei enthält alle vorkonfigurierten API-Requests:

1. **Zuerst:** "Get Token" Request ausführen (Zeile 5) → Token wird automatisch gespeichert
2. **Dann:** Beliebige API-Requests ausführen (z.B. "Neues Projekt erstellen")

**Alternative (Swagger UI):**

1. Öffnen Sie http://localhost:8080/swagger-ui/index.html
2. Klicken Sie auf "Authorize"
3. Fügen Sie Ihr Token ein: `Bearer <your-token>`
4. Testen Sie die Endpoints interaktiv

---

## 📍 Wichtige URLs im Überblick

| Service | URL | Zugangsdaten |
|---------|-----|--------------|
| **Spring Boot API** | http://localhost:8080 | JWT Token erforderlich |
| **Swagger UI** | http://localhost:8080/swagger-ui/index.html | JWT Token erforderlich |
| **OpenAPI JSON** | http://localhost:8080/v3/api-docs | Frei zugänglich |
| **Health Check** | http://localhost:8080/welcome | Frei zugänglich |
| **Keycloak Admin** | http://localhost:9090 | `admin` / `admin` |
| **Employee Service (Mock)** | http://localhost:7070 | Frei zugänglich |
| **PostgreSQL** | localhost:5432 | User: `user`, Passwort: `secret`, DB: `lf8Starter` |

---

## 📚 Detaillierte Dokumentation

### Verfügbare Dokumentationsdateien

1. **`KEYCLOAK_SETUP_GUIDE.md`** - Schritt-für-Schritt Keycloak Einrichtung
2. **`SWAGGER_DOCUMENTATION.md`** - API Dokumentation und Swagger Nutzung
3. **`WIREMOCK_10_EMPLOYEES.md`** - Übersicht über 10 Mock-Mitarbeiter

### API Endpoints

Die API bietet folgende Hauptendpoints:

#### Projektverwaltung
- `POST /projects` - Neues Projekt erstellen
- `GET /projects` - Alle Projekte abrufen
- `GET /projects/{projectId}` - Projekt nach ID abrufen
- `PUT /projects/{projectId}` - Projekt aktualisieren
- `DELETE /projects/{projectId}` - Projekt löschen

#### Mitarbeiterzuordnung
- `POST /projects/{projectId}/employees` - Mitarbeiter zuordnen
- `DELETE /projects/{projectId}/employees/{employeeId}` - Mitarbeiter entfernen
- `GET /projects/{projectId}/employees` - Alle Mitarbeiter eines Projekts abrufen

#### Mitarbeiter-Projekte
- `GET /employees/{employeeId}/projects` - Alle Projekte eines Mitarbeiters abrufen

**Vollständige API-Dokumentation:** Siehe Swagger UI

---

## 🔐 Authentifizierung & Autorisierung

### JWT Token abrufen

Die Anwendung verwendet **OAuth2/OIDC** mit Keycloak als Identity Provider.

**Schnellste Methode:**
1. Öffnen Sie `src/main/ProjectRequests.http`
2. Führen Sie den ersten Request aus ("Get Token")
3. Der Token wird automatisch in allen weiteren Requests verwendet

**Manuell per cURL:**
```bash
curl -X POST http://localhost:9090/realms/hitec-realm/protocol/openid-connect/token ^
  -H "Content-Type: application/x-www-form-urlencoded" ^
  -d "client_id=project-management-service" ^
  -d "client_secret=<IHR-CLIENT-SECRET>" ^
  -d "grant_type=client_credentials"
```

**Token Gültigkeit:** 5 Minuten (standardmäßig)

### Geschützte vs. Öffentliche Endpoints

**Öffentlich (kein Token erforderlich):**
- `/welcome` - Health Check Endpoint
- `/swagger-ui/**` - Swagger UI
- `/v3/api-docs/**` - OpenAPI Dokumentation

**Geschützt (JWT Token erforderlich):**
- Alle `/projects/**` Endpoints
- Alle `/employees/**` Endpoints

---

## 👨‍💼 Employee Service Integration

### WireMock Mock Server

Der Employee Service wird durch **WireMock** simuliert, da der echte Service nicht verfügbar ist.

**10 vorkonfigurierte Mock-Mitarbeiter** (IDs: 1-10)

Beispiele:
- Employee 1: Max Mustermann (Java Senior Developer, Scrum Master)
- Employee 2: Anna Schmidt (Python Expert, Project Manager)
- Employee 3: Thomas Müller (DevOps Engineer, Cloud Architect)

**Vollständige Liste:** Siehe `WIREMOCK_10_EMPLOYEES.md`

### Verfügbare Mock-Endpoints

```
GET /employees/{id}
→ Liefert Mitarbeiterdaten

GET /employees/{id}/qualifications  
→ Liefert Qualifikationen mit Gültigkeitszeiträumen
```

**Mock-Konfiguration anpassen:**
- `wiremock/mappings/*.json` - Request/Response Mappings
- `wiremock/__files/*.json` - Response Bodies

### Circuit Breaker Pattern

Die Integration zum Employee Service ist mit **Resilience4j Circuit Breaker** abgesichert.

**Konfiguration (application.yml):**
```yaml
resilience4j:
  circuitbreaker:
    instances:
      employeeService:
        slidingWindowSize: 10
        failureRateThreshold: 50
        waitDurationInOpenState: 10000ms
```

**Verhalten:**
- Bei 50% Fehlerrate: Circuit Breaker öffnet sich
- Fallback: Temporäre Datenbankeinträge werden verwendet
- Nach 10 Sekunden: Automatischer Reconnect-Versuch

**Testen:**
1. Stoppen Sie WireMock: `docker compose stop employee-service`
2. Versuchen Sie, einen Mitarbeiter zuzuordnen
3. Circuit Breaker öffnet sich → Fehlermeldung mit HTTP 503
4. Starten Sie WireMock: `docker compose start employee-service`
5. Nach 10 Sekunden funktioniert es wieder

---

## 🧩 Business Logic & Validierung

Die Anwendung implementiert folgende Geschäftsregeln:

### 1. Zeitkonflikt-Prüfung

Beim Zuordnen eines Mitarbeiters wird geprüft, ob dieser bereits in einem anderen Projekt im gleichen Zeitraum eingeplant ist.

**Prüfung:**
```java
// Überlappung wird erkannt, wenn:
// (newStart <= existingEnd) AND (newEnd >= existingStart)
```

**Beispiel-Konflikt:**
- Projekt A: 2025-01-01 bis 2025-06-30
- Projekt B: 2025-05-01 bis 2025-12-31
- ❌ Mitarbeiter kann nicht beiden zugeordnet werden

**Fehlermeldung:**
```json
{
  "status": 409,
  "error": "Conflict",
  "message": "Employee is already assigned to another project during this time period",
  "conflictingProjects": [
    {
      "projectId": 1,
      "designation": "Projekt A",
      "startDate": "2025-01-01",
      "plannedEndDate": "2025-06-30"
    }
  ]
}
```

### 2. Qualifikationsprüfung

Beim Zuordnen wird geprüft, ob der Mitarbeiter mindestens eine gültige Qualifikation besitzt.

**Prüfungen:**
- ❌ Mitarbeiter hat keine Qualifikationen → HTTP 422
- ❌ Alle Qualifikationen sind abgelaufen → HTTP 422
- ✅ Mindestens eine Qualifikation ist gültig → OK

**Fehlermeldung bei abgelaufenen Qualifikationen:**
```json
{
  "status": 422,
  "error": "Unprocessable Entity",
  "message": "Employee has no valid qualifications. All qualifications have expired.",
  "expiredQualifications": [
    {
      "name": "Java Developer",
      "level": "SENIOR",
      "validUntil": "2024-12-31"
    }
  ]
}
```

### 3. Duplikatsprüfung

Ein Mitarbeiter kann nicht mehrfach dem gleichen Projekt zugeordnet werden.

**Fehlermeldung:**
```json
{
  "status": 409,
  "error": "Conflict",
  "message": "Employee is already assigned to this project"
}
```

### 4. Weitere Validierungen

- **Projektzeiträume:** `startDate` muss vor `plannedEndDate` liegen
- **Required Fields:** Alle Pflichtfelder müssen ausgefüllt sein
- **Mitarbeiter existiert:** Prüfung über Employee Service
- **Projekt existiert:** Prüfung in lokaler Datenbank

---

## 🗄️ Datenbank

### PostgreSQL Setup

Die Datenbank wird automatisch durch Docker Compose gestartet.

**Verbindungsdaten:**
- Host: `localhost`
- Port: `5432`
- Database: `lf8Starter`
- Username: `user`
- Password: `secret`

### Datenbankschema

**Tabellen:**
1. **`project`** - Projektverwaltung
   - `id`, `designation`, `responsible_employee_id`, `customer_id`, `customer_contact_person`
   - `comment`, `start_date`, `planned_end_date`

2. **`project_employee`** - Zuordnungstabelle
   - `project_id`, `employee_id`

**JPA Entities:**
- `ProjectEntity` - Projekt mit One-to-Many Beziehung zu Mitarbeitern
- `ProjectEmployeeEntity` - Embedded ID für Zuordnung

### Datenbank in IntelliJ einrichten

1. Docker Container mit PostgreSQL starten
2. `src/main/resources/application.yml` öffnen und DB-URL kopieren
3. Rechts: **Database** Tab öffnen
4. **+** → **Data Source from URL**
5. URL einfügen: `jdbc:postgresql://localhost:5432/lf8Starter`
6. PostgreSQL Driver auswählen → **OK**
7. Username: `user`, Password: `secret` → **Apply**
8. **Schemas** Tab: Nur `lf8_starter_db` und `public` aktivieren
9. **Apply** → **OK**

### Datenbank zurücksetzen

Bei Problemen kann die Datenbank komplett zurückgesetzt werden:

```bash
docker compose down
docker volume rm lf8_starter_2025_authentik_lf8starter_postgres_data
docker compose up
```

**Achtung:** Alle Daten gehen verloren!

---

## 🔧 Development & Testing

### Projekt bauen

```bash
# Windows
.\gradlew.bat build

# Linux/Mac
./gradlew build
```

### Tests ausführen

```bash
# Alle Tests
.\gradlew.bat test

# Spezifischen Test
.\gradlew.bat test --tests "*CircuitBreakerIntegrationTest"

# Mit detaillierter Ausgabe
.\gradlew.bat test --info
```

### Test-Coverage

Die Anwendung verfügt über umfangreiche Tests:

**Integration Tests:**
- `CircuitBreakerIntegrationTest` - Circuit Breaker Verhalten
- `EmployeeValidationServiceWireMockTest` - WireMock Integration
- `PostIT`, `GetAllIT`, `GetByMessageIT`, `DeleteIT` - CRUD Operations

**Unit Tests:**
- `GlobalExceptionHandlerTest` - Exception Handling
- `OpenAPIConfigurationTest` - API Dokumentation

**Testcontainers:**
- PostgreSQL Testcontainer für realistische Integrationstests
- Automatisches Setup und Teardown

### Code-Struktur

```
src/
├── main/
│   ├── java/de/szut/lf8_starter/
│   │   ├── project/                    # Projekt-Domain
│   │   │   ├── ProjectController.java
│   │   │   ├── ProjectService.java
│   │   │   ├── ProjectRepository.java
│   │   │   ├── ProjectEntity.java
│   │   │   └── dto/                    # DTOs
│   │   ├── integration/employee/        # Employee Service Integration
│   │   │   ├── EmployeeServiceClient.java
│   │   │   ├── EmployeeValidationService.java
│   │   │   ├── CircuitBreakerService.java
│   │   │   └── dto/                    # Employee DTOs
│   │   ├── security/                    # Security Konfiguration
│   │   │   └── SecurityConfig.java
│   │   ├── exceptionHandling/           # Exception Handling
│   │   │   ├── GlobalExceptionHandler.java
│   │   │   └── ResourceNotFoundException.java
│   │   └── config/                      # Spring Configuration
│   │       └── OpenAPIConfiguration.java
│   └── resources/
│       ├── application.yml              # Hauptkonfiguration
│       └── application.properties
└── test/
    └── java/de/szut/lf8_starter/
        ├── integration/                 # Integration Tests
        └── hello/                       # Beispiel-Tests
```

---

## 🛠️ Technologie-Stack

### Backend Framework
- **Java 22** - Moderne Java Version
- **Spring Boot 3.3.4** - Application Framework
- **Spring Data JPA** - Datenbankzugriff
- **Spring Security** - Authentifizierung & Autorisierung
- **Spring Cloud OpenFeign** - Deklarative HTTP Clients

### Security
- **OAuth2 Resource Server** - JWT Token Validierung
- **Keycloak** - Identity & Access Management
- **JSON Web Tokens (JWT)** - Stateless Authentication

### Resilience
- **Resilience4j Circuit Breaker** - Ausfallsicherheit
- **Resilience4j Retry** - Automatische Wiederholungsversuche

### Database
- **PostgreSQL 16.4** - Produktionsdatenbank
- **Spring Data JPA** - ORM Layer
- **Hibernate** - JPA Implementation

### API Documentation
- **SpringDoc OpenAPI 3** - OpenAPI 3.0 Spezifikation
- **Swagger UI** - Interaktive API Dokumentation

### Testing
- **JUnit 5** - Test Framework
- **Spring Boot Test** - Integration Testing
- **Testcontainers** - Container-basierte Tests
- **WireMock** - HTTP Mock Server
- **Spring Security Test** - Security Testing

### Build & Dependency Management
- **Gradle 8.10** - Build Tool (Gradle Wrapper)
- **Lombok** - Boilerplate Code Reduktion

### Containerization
- **Docker & Docker Compose** - Container Orchestrierung
- **PostgreSQL Container** - Datenbank
- **Keycloak Container** - Identity Provider
- **WireMock Container** - Mock Service

---

## 📖 Verwendung der HTTP Request Files

### ProjectRequests.http

Die Datei `src/main/ProjectRequests.http` enthält alle API-Requests in der richtigen Reihenfolge.

**Verwendung in IntelliJ:**
1. Datei öffnen
2. Grüner Play-Button neben dem Request → Klicken
3. Response wird im Tool-Fenster angezeigt

**Vorkonfigurierte Requests:**

1. **Get Token** - JWT Token abrufen (ZUERST ausführen!)
2. **Neues Projekt erstellen** - POST /projects
3. **Alle Projekte auflisten** - GET /projects
4. **Projekt abrufen** - GET /projects/{id}
5. **Projekt aktualisieren** - PUT /projects/{id}
6. **Mitarbeiter zuordnen** - POST /projects/{id}/employees
7. **Mitarbeiter entfernen** - DELETE /projects/{id}/employees/{employeeId}
8. **Projekt löschen** - DELETE /projects/{id}
9. **Projekte eines Mitarbeiters** - GET /employees/{id}/projects

**Token wird automatisch gespeichert:**
```javascript
> {%
    client.global.set("auth_token", response.body.access_token);
%}
```

Alle folgenden Requests verwenden automatisch den gespeicherten Token:
```
Authorization: Bearer {{auth_token}}
```

---

## 🚨 Troubleshooting

### Keycloak startet nicht

**Problem:** Container stoppt oder bleibt im Restart-Loop

**Lösung:**
```bash
docker compose down
docker volume rm lf8_starter_2025_authentik_keycloak_data
docker compose up
```

### Datenbank-Verbindungsfehler

**Problem:** `Connection refused` oder `Authentication failed`

**Prüfung:**
```bash
# Container-Status prüfen
docker ps

# Logs anschauen
docker logs lf8Starter_postgres

# Container neu starten
docker compose restart postgres-employee
```

### 401 Unauthorized

**Problem:** Alle API-Requests liefern 401

**Ursachen & Lösungen:**
1. **Kein Token:** Token in `ProjectRequests.http` generieren
2. **Token abgelaufen:** Neuen Token generieren (Gültigkeit: 5 Min)
3. **Falsches Client Secret:** Korrektes Secret aus Keycloak kopieren
4. **Keycloak nicht konfiguriert:** `KEYCLOAK_SETUP_GUIDE.md` befolgen

### 503 Service Unavailable

**Problem:** Circuit Breaker ist OPEN

**Diagnose:**
```bash
# WireMock läuft?
docker ps | findstr employee-service

# WireMock erreichbar?
curl http://localhost:7070/__admin/
```

**Lösung:**
```bash
# WireMock neu starten
docker compose restart employee-service

# 10 Sekunden warten (Circuit Breaker Wait Duration)
# Dann erneut versuchen
```

### Tests schlagen fehl

**Problem:** `./gradlew test` liefert Fehler

**Lösungen:**
```bash
# Clean build
.\gradlew.bat clean build

# Nur einen Test ausführen
.\gradlew.bat test --tests "*GetAllIT"

# Mit Debug-Output
.\gradlew.bat test --info --stacktrace
```

### Port bereits belegt

**Problem:** `Address already in use: bind`

**Lösung:**
```bash
# Windows: Prozess auf Port finden
netstat -ano | findstr :8080
netstat -ano | findstr :9090
netstat -ano | findstr :7070

# Prozess beenden (PID aus vorherigem Befehl)
taskkill /PID <PID> /F
```

### Docker Compose Probleme

**Problem:** Services starten nicht korrekt

**Lösung:**
```bash
# Alles stoppen und entfernen
docker compose down -v

# Images neu pullen
docker compose pull

# Neu starten
docker compose up
```

---

## 📝 Beispiel-Workflow

### Komplettes Durchlaufen der Anwendung

**1. Setup:**
```bash
# Services starten
docker compose up

# Warten bis Keycloak bereit ist (~60 Sekunden)
# In neuem Terminal: Anwendung starten
.\gradlew.bat bootRun
```

**2. Keycloak konfigurieren:**
- http://localhost:9090 öffnen
- `KEYCLOAK_SETUP_GUIDE.md` befolgen
- Client Secret kopieren

**3. API testen:**

Öffnen Sie `src/main/ProjectRequests.http`:

```http
### 1. Token holen
POST http://localhost:9090/realms/hitec-realm/protocol/openid-connect/token
...

### 2. Projekt erstellen
POST http://localhost:8080/projects
{
  "designation": "Website Relaunch",
  "responsibleEmployeeId": 1,
  "customerId": 100,
  "customerContactPerson": "Max Mustermann",
  "comment": "Kompletter Relaunch der Firmenwebsite",
  "startDate": "2025-11-10",
  "plannedEndDate": "2026-03-31"
}

### 3. Mitarbeiter zuordnen
POST http://localhost:8080/projects/1/employees
{
  "employeeId": 2
}

### 4. Alle Projekte abrufen
GET http://localhost:8080/projects

### 5. Projekte des Mitarbeiters abrufen
GET http://localhost:8080/employees/2/projects
```

**4. Geschäftslogik testen:**

```http
### Zeitkonflikt provozieren
# Projekt 1: 2025-11-10 bis 2026-03-31
# Mitarbeiter 2 zuordnen ✅

# Projekt 2 erstellen: 2025-12-01 bis 2026-06-30
# Mitarbeiter 2 zuordnen ❌ Konflikt!

### Abgelaufene Qualifikation testen
# Mitarbeiter mit abgelaufenen Qualifikationen zuordnen
# → 422 Unprocessable Entity
```

---

## 🎓 Lernziele & Bewertungskriterien

### Implementierte Konzepte

Diese Anwendung demonstriert folgende Konzepte aus dem Unterricht:

✅ **RESTful API Design**
- HTTP-Methoden korrekt eingesetzt (GET, POST, PUT, DELETE)
- Ressourcen-orientierte URL-Struktur
- Korrekte HTTP Status Codes

✅ **Spring Boot Architektur**
- Controller → Service → Repository Pattern
- Dependency Injection
- Configuration Management

✅ **Datenbankintegration**
- JPA Entities & Relationships
- Repository Pattern
- Transaction Management

✅ **Security**
- OAuth2/OIDC mit Keycloak
- JWT Token Validierung
- Role-based Access Control

✅ **Microservice-Kommunikation**
- OpenFeign Clients
- Circuit Breaker Pattern
- Fallback-Strategien

✅ **Exception Handling**
- Global Exception Handler
- Custom Exceptions
- Structured Error Responses

✅ **API-Dokumentation**
- OpenAPI 3.0 Annotations
- Swagger UI Integration
- Vollständige Request/Response Dokumentation

✅ **Testing**
- Integration Tests
- Testcontainers
- WireMock für External Services

---

## 📋 Checkliste für Abgabe

### Vor der Abgabe prüfen:

- [x] **Docker Compose** startet alle Services erfolgreich
- [x] **Keycloak** ist konfiguriert (Realm, Client, Secret)
- [x] **Spring Boot** Anwendung startet ohne Fehler
- [x] **Token-Generierung** funktioniert (`ProjectRequests.http`)
- [x] **API-Requests** funktionieren (Projekt erstellen, auflisten, etc.)
- [x] **Swagger UI** ist erreichbar und funktioniert
- [x] **Tests** laufen durch (`.\gradlew.bat test`)
- [x] **Dokumentation** ist vollständig (diese README.md)

### Wichtige Hinweise für den Lehrer:

1. **Client Secret:** Das Secret in `ProjectRequests.http` muss aus Keycloak kopiert werden
2. **Wartezeit:** Keycloak benötigt ca. 60 Sekunden zum Starten
3. **Port 7070:** Employee Service Mock läuft auf Port 7070 (Docker Compose Port Mapping)
4. **Mock-Daten:** 10 Mitarbeiter (IDs 1-10) sind vorkonfiguriert

---

## 👥 Autor

**Entwickelt von:** Alexander Zimmermann  
**Projekt:** LF08 - Anwendungsentwicklung  
**Schule:** SZ Utbremen  
**Datum:** November 2025

---

## 📄 Lizenz

Dieses Projekt dient ausschließlich Bildungszwecken im Rahmen des LF08-Unterrichts.

---

## 🔗 Weiterführende Links

- [Spring Boot Dokumentation](https://spring.io/projects/spring-boot)
- [Keycloak Dokumentation](https://www.keycloak.org/documentation)
- [Resilience4j Guide](https://resilience4j.readme.io/)
- [WireMock Dokumentation](https://wiremock.org/docs/)
- [OpenAPI Specification](https://spec.openapis.org/oas/v3.0.0)
- [Spring Security OAuth2](https://spring.io/guides/tutorials/spring-boot-oauth2)

---

## ❓ Häufig gestellte Fragen (FAQ)

### Wie lange ist der JWT Token gültig?
Standard: 5 Minuten. Danach muss ein neuer Token generiert werden.

### Kann ich die Mock-Daten anpassen?
Ja, in den Dateien unter `wiremock/mappings/` und `wiremock/__files/`.

### Warum verwendet das Projekt Java 22?
Um moderne Java-Features zu nutzen. Gradle Wrapper stellt sicher, dass die korrekte Version verwendet wird.

### Muss ich Keycloak jedes Mal neu konfigurieren?
Nein, die Konfiguration wird in einem Docker Volume gespeichert. Nur bei `docker compose down -v` geht sie verloren.

### Kann ich einen echten Employee Service anbinden?
Ja, ändern Sie einfach die URL in `application.yml` unter `employee-service.base-url`.

### Wie kann ich weitere Mitarbeiter hinzufügen?
Erstellen Sie neue JSON-Dateien in `wiremock/mappings/` nach dem Muster der existierenden Dateien.

---

**📧 Bei Fragen oder Problemen: Siehe Troubleshooting-Sektion oder Dokumentationsdateien im Projekt.**

