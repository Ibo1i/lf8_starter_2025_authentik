# Starter für das LF08 Projekt - Project Management Service

Dieses Projekt implementiert einen **Project Management Service** mit folgenden Features:
- ✅ JWT-Authentifizierung (Keycloak)
- ✅ Employee-Service Integration mit Circuit Breaker
- ✅ PostgreSQL Datenbank
- ✅ RESTful API mit OpenAPI/Swagger
- ✅ Resilience4j für Fehlertoleranz

## 🚀 Schnellstart

### Voraussetzungen
* Docker: https://docs.docker.com/get-docker/
* Docker Compose (bei Windows und Mac in Docker enthalten): https://docs.docker.com/compose/install/

### Alle Services starten

```bash
docker compose up
```

**Das startet automatisch:**
1. ✅ PostgreSQL Datenbank (Port 5432)
2. ✅ Keycloak (Port 9090)
3. ✅ Employee-Service Mock (WireMock auf Port 8081)

**⏱️ Wartezeit:** ~60 Sekunden bis Keycloak vollständig gestartet ist.

### Anwendung starten

**Option A: Mit Gradle (lokal entwickeln)**
```bash
# Windows
.\gradlew.bat bootRun

# Linux/Mac
./gradlew bootRun
```

**Option B: Mit Docker (wie in Produktion)**
```bash
# TODO: Dockerfile erstellen falls gewünscht
```

## 📍 Wichtige Endpunkte

| Service | URL | Beschreibung |
|---------|-----|--------------|
| **API** | http://localhost:8080 | Project Management Service |
| **Swagger UI** | http://localhost:8080/swagger | API-Dokumentation |
| **Keycloak Admin** | http://localhost:9090 | Admin: `admin` / `admin` |
| **Employee-Service Mock** | http://localhost:8081 | WireMock Mock-Service |
| **PostgreSQL** | localhost:5432 | DB: `lf8Starter`, User: `user`, PW: `secret` |

## 🔐 Authentifizierung

### JWT Token holen

1. Datei öffnen: `GetBearerToken.http`
2. Request ausführen (grüner Pfeil in IntelliJ)
3. `access_token` aus Response kopieren
4. Token in weitere Requests einfügen: `Authorization: Bearer <token>`

**Oder direkt per cURL:**
```bash
curl -X POST http://localhost:9090/realms/hitec-realm/protocol/openid-connect/token \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "client_id=project-management-service" \
  -d "client_secret=jBCaEJraTUdFX1CzbHvxGLrxZmGmggrF" \
  -d "grant_type=client_credentials"
```

## 🧪 API Testen

### Mit HTTP-Dateien (IntelliJ)

Alle Requests vorbereitet in: `src/main/ProjectRequests.http`

**Ablauf:**
1. Token holen (siehe oben)
2. Projekt erstellen: `POST /projects`
3. Mitarbeiter zuweisen: `POST /projects/{id}/employees`

### Mit Swagger UI

1. Öffne http://localhost:8080/swagger
2. Klicke auf "Authorize"
3. Token einfügen: `Bearer <dein-token>`
4. Requests ausführen

## 👨‍💼 Employee-Service Mock

Der Employee-Service wird durch **WireMock** simuliert.

### Verfügbare Mock-Endpoints

```
GET /employees/E-{id}
→ Gibt Mitarbeiter-Details zurück

GET /employees/E-{id}/qualifications  
→ Gibt Qualifikationen zurück
```

### Beispiel-Antworten

**Employee:**
```json
{
  "employeeId": "E-1",
  "firstName": "Max",
  "lastName": "Mustermann",
  "email": "max.mustermann@hitec.de",
  "status": "ACTIVE"
}
```

**Qualifications:**
```json
{
  "employeeId": "E-1",
  "qualifications": [
    {
      "name": "Java Senior Developer",
      "level": "SENIOR",
      "validUntil": "2026-12-31"
    }
  ]
}
```

### Mock-Konfiguration anpassen

Mock-Daten befinden sich in:
- `wiremock/mappings/*.json` - Request/Response Mappings
- `wiremock/__files/*.json` - Response Bodies (optional)

## 🗄️ Datenbank

## 🗄️ Datenbank

### Services starten
```bash
docker compose up
```
**Hinweis:** Container laufen dauerhaft! Stoppen wenn nicht benötigt.

### Services stoppen
```bash
docker compose down
```

### Datenbank zurücksetzen (bei Problemen)
```bash
docker compose down
docker volume rm lf8_starter_2025_authentik_lf8Starter_postgres_data
docker compose up
```

### PostgreSQL in IntelliJ einrichten

1. Docker-Container mit PostgreSQL starten
2. `src/main/resources/application.yml` öffnen und DB-URL kopieren
3. Rechts: **Database** Reiter öffnen
4. Auf Datenbanksymbol mit Schlüssel klicken
5. **+** → **Data Source from URL**
6. URL einfügen: `jdbc:postgresql://localhost:5432/lf8Starter`
7. PostgreSQL-Treiber auswählen → **OK**
8. Username: `user`, Password: `secret` → **Apply**
9. **Schemas** Tab: Nur `lf8_starter_db` und `public` aktivieren
10. **Apply** → **OK**

## 🔧 Entwicklung

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

# Spezifischer Test
.\gradlew.bat test --tests "*CircuitBreakerIntegrationTest"
```

### Code-Struktur

```
src/
├── main/
│   ├── java/de/szut/lf8_starter/
│   │   ├── project/              # Projekt-Management Domain
│   │   ├── integration/employee/ # Employee-Service Integration
│   │   ├── security/             # JWT & Keycloak Config
│   │   └── exceptionHandling/    # Globale Exception Handler
│   └── resources/
│       ├── application.yml       # Hauptkonfiguration
│       └── application-local.yml # Lokale Entwicklung
└── test/                         # Unit & Integration Tests
```

## 📚 User Stories & Features

### ✅ Story 4.1: JWT-Authentifizierung
- Keycloak Integration
- Bearer Token für alle Endpoints
- Rolle: `hitec-employee` erforderlich

### ✅ Story 4.2: Employee-Service Integration
- OpenFeign Client
- Circuit Breaker (Resilience4j)
- Retry-Mechanismus
- Timeout: 3 Sekunden
- Fehlerbehandlung für alle Szenarien:
  - 404: Mitarbeiter nicht gefunden
  - 422: Qualifikation fehlt/abgelaufen
  - 502: Service Error
  - 503: Circuit Breaker offen
  - 504: Timeout

## 🛡️ Circuit Breaker

Der Circuit Breaker schützt vor Employee-Service Ausfällen:

| Parameter | Wert |
|-----------|------|
| Failure Rate Threshold | 50% |
| Slow Call Duration | 3 Sekunden |
| Sliding Window Size | 10 Requests |
| Wait Duration (Open) | 60 Sekunden |
| Permitted Calls (Half-Open) | 3 |

**Status prüfen:**
```
http://localhost:8080/actuator/circuitbreakers
```

## 📖 Weitere Dokumentation

- `EMPLOYEE_SERVICE_INTEGRATION.md` - Employee-Service Details
- `EMPLOYEE_SERVICE_MOCK_SETUP.md` - Mock-Service Setup
- `KEYCLOAK_SETUP_ANLEITUNG.md` - Keycloak Konfiguration
- `src/main/ProjectRequests.http` - Beispiel-Requests

## ❓ Troubleshooting

### "employee-service executing GET http://employee-service:8080"
→ **Lösung:** WireMock Mock-Service läuft nicht
```bash
docker compose up employee-service
```

### "JWT-Token ist ungültig oder abgelaufen"
→ **Lösung:** Neuen Token holen (siehe Authentifizierung)

### Keycloak startet nicht
→ **Lösung:** Länger warten (~60 Sekunden) oder Logs prüfen:
```bash
docker logs hitec-keycloak
```

### Datenbank-Verbindungsfehler
→ **Lösung:** PostgreSQL läuft nicht:
```bash
docker compose up postgres-employee
```

## 👥 Mitwirkende

- Entwickelt für LF8 Schulprojekt
- Technologie-Stack: Spring Boot 3, Java 17, PostgreSQL, Keycloak, WireMock

## 📄 Lizenz

Schulprojekt - Keine kommerzielle Nutzung

---

**Viel Erfolg! 🚀**

