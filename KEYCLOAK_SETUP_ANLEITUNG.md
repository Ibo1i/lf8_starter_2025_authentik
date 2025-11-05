# Keycloak JWT-Authentifizierung - Komplette Anleitung

## Übersicht
Diese Anleitung führt Sie Schritt für Schritt durch die Einrichtung von Keycloak und die Verwendung von JWT-Tokens für authentifizierte API-Anfragen.

---

## ⚡ Quick Start für IntelliJ-Nutzer

**Gute Nachrichten!** Alle API-Requests sind bereits vorbereitet in:
📁 **`src/main/ProjectRequests.http`**

**Sie müssen nur:**
1. ✅ Keycloak konfigurieren (Kapitel 2)
2. ✅ Client Secret in die Datei eintragen (Kapitel 4.2.2)
3. ✅ Token-Request ausführen (Kapitel 4.2.3)
4. ✅ Beliebige API-Requests nutzen (Kapitel 5.2)

**➡️ Direkt zu Kapitel 2 springen, wenn Sie IntelliJ verwenden!**

---

## 1. Docker Container starten

### 1.1 Docker Desktop sicherstellen
Stellen Sie sicher, dass Docker Desktop läuft.

### 1.2 Keycloak Container starten
Öffnen Sie ein Terminal/PowerShell im Projektverzeichnis und führen Sie aus:

```bash
docker-compose up
```

**Erwartete Ausgabe:**
```
[+] Running 3/3
 ✔ Network lf8_starter_2025_authentik_default  Created
 ✔ Container hitec-keycloak                    Started
 ✔ Container lf8Starter_postgres               Started
```

### 1.3 Container-Status überprüfen
```bash
docker-compose ps
```

**Erwartete Ausgabe:**
```
NAME                IMAGE                           STATUS
hitec-keycloak      quay.io/keycloak/keycloak:23.0  Up .. seconds (healthy)
lf8Starter_postgres postgres:16.4                   Up .. seconds
```

⚠️ **Wichtig:** Warten Sie, bis der Status `Up (healthy)` zeigt!

---

## 2. Keycloak-Konfiguration im Browser

### 2.1 Keycloak Admin Console öffnen
1. Öffnen Sie Ihren Browser
2. Navigieren Sie zu: **http://localhost:9090**
3. Klicken Sie auf **"Administration Console"**

### 2.2 Anmelden
- **Username:** `admin`
- **Password:** `admin`
- Klicken Sie auf **"Sign In"**

### 2.3 Realm auswählen
1. Oben links im Dropdown-Menü sollte **"hitec-realm"** bereits vorhanden sein
2. Falls nicht, erstellen Sie einen neuen Realm:
   - Klicken Sie auf das Dropdown-Menü oben links
   - Klicken Sie auf **"Create Realm"**
   - **Realm name:** `hitec-realm`
   - **Enabled:** `ON` ✓
   - Klicken Sie auf **"Create"**

### 2.4 Client konfigurieren

#### 2.4.1 Client überprüfen/erstellen
1. Klicken Sie im linken Menü auf **"Clients"**
2. Suchen Sie nach **"project-management-service"**

**Falls der Client nicht existiert:**
1. Klicken Sie auf **"Create client"**
2. **General Settings:**
   - **Client type:** `OpenID Connect`
   - **Client ID:** `project-management-service`
   - Klicken Sie auf **"Next"**
3. **Capability config:**
   - **Client authentication:** `ON` ✓
   - **Authorization:** `OFF`
   - **Standard flow:** `ON` ✓
   - **Direct access grants:** `ON` ✓
   - **Service accounts roles:** `ON` ✓
   - Klicken Sie auf **"Next"**
4. **Login settings:**
   - **Valid redirect URIs:** `http://localhost:8080/*`
   - **Valid post logout redirect URIs:** `http://localhost:8080/*`
   - **Web origins:** `*`
   - Klicken Sie auf **"Save"**

#### 2.4.2 Client Secret abrufen
1. Wählen Sie den Client **"project-management-service"**
2. Klicken Sie auf den Tab **"Credentials"**
3. Kopieren Sie den **"Client secret"** (z.B. `abc123xyz...`)
   
   📋 **WICHTIG:** Notieren Sie sich dieses Secret!

### 2.5 Client Role erstellen

1. Bleiben Sie beim Client **"project-management-service"**
2. Klicken Sie auf den Tab **"Roles"**
3. Klicken Sie auf **"Create role"**
4. **Role name:** `hitec-employee`
5. **Description:** `Standard role for HiTec employees`
6. Klicken Sie auf **"Save"**

### 2.6 Service Account Roles zuweisen

1. Bleiben Sie beim Client **"project-management-service"**
2. Klicken Sie auf den Tab **"Service account roles"**
3. Klicken Sie auf **"Assign role"**
4. Filtern Sie nach **"Filter by clients"**
5. Suchen Sie nach **"hitec-employee"** (unter project-management-service)
6. Wählen Sie die Checkbox bei **"hitec-employee"**
7. Klicken Sie auf **"Assign"**

### 2.7 Benutzer erstellen (Optional - für User Login)

Falls Sie mit einem echten Benutzer testen möchten:

1. Klicken Sie im linken Menü auf **"Users"**
2. Klicken Sie auf **"Add user"**
3. **Username:** `testuser`
4. **Email:** `testuser@hitec.de`
5. **First name:** `Test`
6. **Last name:** `User`
7. Klicken Sie auf **"Create"**
8. Wechseln Sie zum Tab **"Credentials"**
9. Klicken Sie auf **"Set password"**
10. **Password:** `test123`
11. **Password confirmation:** `test123`
12. **Temporary:** `OFF`
13. Klicken Sie auf **"Save"**

#### 2.7.1 Rolle dem Benutzer zuweisen
1. Bleiben Sie beim User **"testuser"**
2. Klicken Sie auf den Tab **"Role mapping"**
3. Klicken Sie auf **"Assign role"**
4. Filtern Sie nach **"Filter by clients"**
5. Wählen Sie **"hitec-employee"** (unter project-management-service)
6. Klicken Sie auf **"Assign"**

---

## 3. Spring Boot Anwendung starten

### 3.1 Application Properties überprüfen

Stellen Sie sicher, dass `application.properties` folgende Werte hat:

```properties
# Keycloak/Authentik Configuration
spring.security.oauth2.resourceserver.jwt.issuer-uri=http://localhost:9090/realms/hitec-realm
spring.security.oauth2.resourceserver.jwt.jwk-set-uri=http://localhost:9090/realms/hitec-realm/protocol/openid-connect/certs
```

### 3.2 Anwendung starten

In IntelliJ IDEA:
1. Öffnen Sie die Klasse `Lf8StarterApplication`
2. Klicken Sie auf das grüne Play-Symbol
3. Warten Sie, bis die Anwendung vollständig gestartet ist

**Erwartete Ausgabe im Log:**
```
Started Lf8StarterApplication in X.XXX seconds
```

---

## 4. JWT-Token abrufen

### 4.1 Via Postman

#### 4.1.1 Neue Request erstellen
1. Öffnen Sie Postman
2. Erstellen Sie eine neue **POST** Request

#### 4.1.2 Token-Endpoint konfigurieren
- **Method:** `POST`
- **URL:** `http://localhost:9090/realms/hitec-realm/protocol/openid-connect/token`

#### 4.1.3 Headers setzen
Klicken Sie auf den Tab **"Headers"** und fügen Sie hinzu:
- **Key:** `Content-Type`
- **Value:** `application/x-www-form-urlencoded`

#### 4.1.4 Body konfigurieren
Klicken Sie auf den Tab **"Body"**:
1. Wählen Sie **"x-www-form-urlencoded"**
2. Fügen Sie folgende Key-Value Paare hinzu:

**Für Service Account (empfohlen):**
| Key | Value |
| `grant_type` | `client_credentials` |
| `client_id` | `project-management-service` |
| `client_secret` | `[IHR-CLIENT-SECRET]` |

**ODER für User Login:**
| Key | Value |
| `grant_type` | `password` |
| `client_id` | `project-management-service` |
| `client_secret` | `[IHR-CLIENT-SECRET]` |
| `username` | `testuser` |
| `password` | `test123` |

#### 4.1.5 Request senden
1. Klicken Sie auf **"Send"**
2. **Erwartete Response (200 OK):**

```json
{
    "access_token": "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCIsImtpZCI6Ii4uLiJ9.eyJleHAiOjE3MzA4MTk1MDUsImlhdCI6MTczMDgxOTIwNSwianRpIjoiLi4uIiwiaXNzIjoiaHR0cDovL2xvY2FsaG9zdDo5MDkwL3JlYWxtcy9oaXRlYy1yZWFsbSIsInN1YiI6Ii4uLiIsInJlc291cmNlX2FjY2VzcyI6eyJwcm9qZWN0LW1hbmFnZW1lbnQtc2VydmljZSI6eyJyb2xlcyI6WyJoaXRlYy1lbXBsb3llZSJdfX19.signature...",
    "expires_in": 300,
    "token_type": "Bearer",
    "scope": "profile email"
}
```

3. **Kopieren Sie den kompletten `access_token` Wert!**

### 4.2 Via IntelliJ HTTP Client (⭐ EMPFOHLEN - einfacher!)

#### 4.2.1 ProjectRequests.http verwenden
Das Projekt enthält bereits eine **fertig konfigurierte** Datei für alle API-Requests!

1. Öffnen Sie die Datei: **`src/main/ProjectRequests.http`**
2. Diese Datei enthält ALLE Requests die Sie brauchen, inkl. Token-Abruf!

#### 4.2.2 Client Secret eintragen (EINMALIG)

Finden Sie in der Datei diese Zeile (ca. Zeile 13):

```http
&client_secret=jBCaEJraTUdFX1CzbHvxGLrxZmGmggrF
```

**✏️ Ersetzen Sie diesen Wert mit Ihrem echten Client Secret aus Keycloak!**

So sollte es dann aussehen:
```http
# @name getToken
POST http://localhost:9090/realms/hitec-realm/protocol/openid-connect/token
Content-Type: application/x-www-form-urlencoded

client_id=project-management-service
&client_secret=abc123xyz...IHR-ECHTES-SECRET
&grant_type=client_credentials
&scope=openid
```

#### 4.2.3 Token automatisch holen 🎯
1. Scrollen Sie zum **ersten Request** (Zeile 8-20) in der Datei
2. Klicken Sie auf das **grüne Play-Symbol** (▶️) links neben:
   ```http
   ### JWT Token von Keycloak holen (Story 4.1)
   # @name getToken
   POST http://localhost:9090/realms/hitec-realm/protocol/openid-connect/token
   ```

3. **Erwartetes Ergebnis im Response-Fenster:**
   ```
   ✅ Token gespeichert! Gültig für: 300 Sekunden
   ```

**🎉 Der Token wird AUTOMATISCH in der Variable `{{auth_token}}` gespeichert!**

Sie müssen den Token **NICHT** manuell kopieren - alle nachfolgenden Requests in der Datei verwenden automatisch:
```http
Authorization: Bearer {{auth_token}}
```

---

## 5. Authentifizierte API-Requests

### 5.1 Via Postman

#### 5.1.1 GET /projects Request erstellen
1. Erstellen Sie eine neue **GET** Request
2. **URL:** `http://localhost:8080/projects`

#### 5.1.2 Authorization Header setzen
Klicken Sie auf den Tab **"Headers"** und fügen Sie hinzu:
- **Key:** `Authorization`
- **Value:** `Bearer [IHR-ACCESS-TOKEN]`

**Beispiel:**
```
Bearer eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCIsImtpZCI6Ii4uLiJ9.eyJleHAiOjE3MzA4MTk1MDUsImlhdCI6MTczMDgxOTIwNSwianRpIjoiLi4uIiwiaXNzIjoiaHR0cDovL2xvY2FsaG9zdDo5MDkwL3JlYWxtcy9oaXRlYy1yZWFsbSIsInN1YiI6Ii4uLiIsInJlc291cmNlX2FjY2VzcyI6eyJwcm9qZWN0LW1hbmFnZW1lbnQtc2VydmljZSI6eyJyb2xlcyI6WyJoaXRlYy1lbXBsb3llZSJdfX19.signature...
```

#### 5.1.3 Request senden
1. Klicken Sie auf **"Send"**
2. **Erwartete Response (200 OK):**

```json
[
    {
        "id": 1,
        "name": "Projekt A",
        "description": "Beschreibung...",
        ...
    }
]
```

### 5.2 Via IntelliJ HTTP Client (⭐ Super einfach!)

#### 5.2.1 Alle Requests sind bereits fertig!

Die Datei `src/main/ProjectRequests.http` enthält **ALLE** fertigen Requests:

- ✅ **Token holen** (erster Request)
- ✅ **Neues Projekt erstellen** (POST /projects)
- ✅ **Alle Projekte auflisten** (GET /projects)
- ✅ **Projekt abrufen** (GET /projects/{id})
- ✅ **Projekt aktualisieren** (PUT /projects/{id})
- ✅ **Projekt löschen** (DELETE /projects/{id})
- ✅ **Mitarbeiter zuweisen** (POST /projects/{id}/employees)
- ✅ **Mitarbeiter entfernen** (DELETE /projects/{id}/employees/{employeeId})
- ✅ **Mitarbeiterliste** (GET /projects/{id}/employees)
- ✅ **Mitarbeiter-Projekte** (GET /employees/{id}/projects)

#### 5.2.2 So verwenden Sie die Requests

**Schritt 1:** Token holen (nur einmal)
```http
### JWT Token von Keycloak holen (Story 4.1)
# @name getToken
POST http://localhost:9090/realms/hitec-realm/protocol/openid-connect/token
```
▶️ Klicken Sie auf das grüne Play-Symbol

**Schritt 2:** Beliebigen API-Request ausführen

Zum Beispiel - Alle Projekte auflisten:
```http
### Alle Projekte auflisten
GET http://localhost:8080/projects
Authorization: Bearer {{auth_token}}
```
▶️ Klicken Sie auf das grüne Play-Symbol

**Das war's! 🎉** Der Token wird automatisch verwendet!

#### 5.2.3 Beispiel-Requests

**Neues Projekt erstellen:**
```http
### Neues Projekt erstellen
POST http://localhost:8080/projects
Content-Type: application/json
Authorization: Bearer {{auth_token}}

{
  "designation": "Mein cooles Projekt",
  "responsibleEmployeeId": 1,
  "customerId": 1,
  "customerContactPerson": "Max Mustermann",
  "comment": "Dies ist ein Test-Projekt",
  "startDate": "2025-11-10",
  "plannedEndDate": "2026-05-31"
}
```

**Mitarbeiter zuweisen:**
```http
### Mitarbeiter einem Projekt zuweisen
POST http://localhost:8080/projects/1/employees
Content-Type: application/json
Authorization: Bearer {{auth_token}}

{
  "employeeId": 1,
  "qualification": "Java"
}
```

💡 **Tipp:** Alle diese Requests sind bereits in der Datei vorbereitet - Sie müssen nur auf Play klicken!

---

## 6. Alle API-Endpoints testen

### 6.1 Mit IntelliJ HTTP Client (empfohlen)

**Öffnen Sie:** `src/main/ProjectRequests.http`

Diese Datei enthält **fertige Requests für alle Endpoints**:

#### 6.1.1 Workflow
1. **Token holen** (oberster Request) → ▶️ Play
2. **Beliebigen Endpoint testen** → ▶️ Play
3. Token läuft nach 5 Minuten ab? → Schritt 1 wiederholen

#### 6.1.2 Verfügbare Requests in der Datei

**Projekt-Management:**
- ✅ `POST /projects` - Neues Projekt erstellen
- ✅ `GET /projects` - Alle Projekte auflisten  
- ✅ `GET /projects/{id}` - Projekt abrufen
- ✅ `PUT /projects/{id}` - Projekt aktualisieren
- ✅ `DELETE /projects/{id}` - Projekt löschen

**Mitarbeiter-Zuweisungen:**
- ✅ `POST /projects/{id}/employees` - Mitarbeiter zuweisen
- ✅ `DELETE /projects/{id}/employees/{employeeId}` - Mitarbeiter entfernen
- ✅ `GET /projects/{id}/employees` - Mitarbeiterliste
- ✅ `GET /employees/{id}/projects` - Projekte eines Mitarbeiters

💡 **Alle verwenden automatisch:** `Authorization: Bearer {{auth_token}}`

### 6.2 Öffentliche Endpoints (ohne Token)

Diese Endpoints funktionieren OHNE Token:

```http
GET http://localhost:8080/actuator/health
GET http://localhost:8080/swagger-ui/index.html
GET http://localhost:8080/v3/api-docs
```

### 6.3 Mit Postman testen

---

## 7. Troubleshooting

### Problem: 401 Unauthorized

**Mögliche Ursachen:**
1. **Token fehlt oder ist falsch formatiert**
   - Prüfen Sie, ob der Header `Authorization: Bearer [TOKEN]` korrekt ist
   - Achten Sie auf das Leerzeichen zwischen "Bearer" und dem Token

2. **Token ist abgelaufen**
   - JWT-Tokens sind nur 5 Minuten gültig
   - Holen Sie sich einen neuen Token

3. **Keycloak ist nicht erreichbar**
   - Prüfen Sie: `docker-compose ps`
   - Stellen Sie sicher, dass hitec-keycloak läuft

**Lösung:**
```bash
# Neuen Token holen
POST http://localhost:9090/realms/hitec-realm/protocol/openid-connect/token
```

### Problem: 403 Forbidden

**Ursache:** Die Rolle `hitec-employee` fehlt im Token

**Fehlermeldung:**
```json
{
    "timestamp": "2025-11-05T14:56:20.906541300",
    "status": 403,
    "error": "Forbidden",
    "message": "Unzureichende Berechtigungen. Erforderliche Rolle: hitec-employee",
    "path": "/projects",
    "requiredRoles": ["hitec-employee"],
    "userRoles": ["default-roles-hitec-realm", "offline_access", "uma_authorization"]
}
```

**Lösung:**
1. Gehen Sie zu Keycloak: http://localhost:9090
2. Wählen Sie den Client **"project-management-service"**
3. Tab **"Service account roles"**
4. Stellen Sie sicher, dass **"hitec-employee"** zugewiesen ist
5. Holen Sie sich einen **neuen** Token

### Problem: 500 Internal Server Error beim /actuator/health

**Ursache:** Der Actuator-Endpoint ist nicht korrekt konfiguriert

**Lösung in `application.properties`:**
```properties
management.endpoints.web.exposure.include=health
management.endpoint.health.show-details=always
```

### Problem: JWT signature verification failed

**Fehlermeldung:**
```json
{
    "message": "JWT-Token ist ungültig oder abgelaufen.",
    "details": "Signed JWT rejected: Another algorithm expected, or no matching key(s) found"
}
```

**Ursachen:**
1. **Falscher issuer-uri in application.properties**
   - Stellen Sie sicher: `http://localhost:9090/realms/hitec-realm`
   - NICHT `http://localhost:8080/...`

2. **Keycloak läuft auf falschem Port**
   - Prüfen Sie `docker-compose.yml`
   - Port sollte `9090:9090` sein

**Lösung:**
```properties
# Korrekte Konfiguration
spring.security.oauth2.resourceserver.jwt.issuer-uri=http://localhost:9090/realms/hitec-realm
spring.security.oauth2.resourceserver.jwt.jwk-set-uri=http://localhost:9090/realms/hitec-realm/protocol/openid-connect/certs
```

### Problem: Keycloak Container startet nicht

**Fehler:**
```
Error: ports are not available: exposing port TCP 0.0.0.0:8080
```

**Ursache:** Port 8080 ist bereits belegt (von Ihrer Spring Boot App)

**Lösung:**
Keycloak läuft auf Port **9090**, nicht 8080:
```yaml
# docker-compose.yml
keycloak:
  ports:
    - "9090:9090"
```

---

## 8. JWT-Token Details verstehen

### 8.1 Token-Struktur

Ein JWT besteht aus 3 Teilen, getrennt durch Punkte:
```
HEADER.PAYLOAD.SIGNATURE
```

### 8.2 Token dekodieren (nur zu Debugging-Zwecken)

Besuchen Sie: https://jwt.io

Fügen Sie Ihren Token ein und sehen Sie die dekodierten Claims:

```json
{
  "exp": 1730819505,
  "iat": 1730819205,
  "jti": "5b206f97-5455-4146-b9e3-3b87e01db5c8",
  "iss": "http://localhost:9090/realms/hitec-realm",
  "sub": "c662451d-2a74-4c7e-a2ee-fbc501d1ff82",
  "typ": "Bearer",
  "resource_access": {
    "project-management-service": {
      "roles": ["hitec-employee"]
    }
  },
  "preferred_username": "service-account-project-management-service",
  "client_id": "project-management-service"
}
```

**Wichtige Claims:**
- `iss`: Issuer (muss Keycloak sein)
- `exp`: Expiration Time (Unix timestamp)
- `resource_access.project-management-service.roles`: Enthält `hitec-employee`

---

## 9. Postman Collection Export

Sie können eine Postman Collection für alle Requests erstellen:

### 9.1 Collection erstellen
1. Klicken Sie in Postman auf **"New"** → **"Collection"**
2. Name: `HiTec Project Management API`

### 9.2 Environment Variable für Token
1. Klicken Sie auf **"Environments"**
2. Erstellen Sie ein neues Environment: `HiTec Dev`
3. Fügen Sie Variable hinzu:
   - **Variable:** `access_token`
   - **Type:** `default`
   - **Initial Value:** (leer lassen)
   - **Current Value:** `[HIER-TOKEN-EINFÜGEN]`

### 9.3 Collection Authorization
1. Wählen Sie die Collection
2. Tab **"Authorization"**
3. **Type:** `Bearer Token`
4. **Token:** `{{access_token}}`

Nun erben alle Requests in der Collection automatisch den Token!

---

## 10. Quick Reference - Cheat Sheet

### Token holen (PowerShell/CMD):
```powershell
curl -X POST http://localhost:9090/realms/hitec-realm/protocol/openid-connect/token ^
  -H "Content-Type: application/x-www-form-urlencoded" ^
  -d "grant_type=client_credentials&client_id=project-management-service&client_secret=IHR-SECRET"
```

### API Request mit Token (PowerShell/CMD):
```powershell
curl -X GET http://localhost:8080/projects ^
  -H "Authorization: Bearer IHR-TOKEN"
```

### Wichtige URLs:
- **Keycloak Admin Console:** http://localhost:9090
- **API Base URL:** http://localhost:8080
- **Swagger UI:** http://localhost:8080/swagger-ui/index.html
- **Health Check:** http://localhost:8080/actuator/health

### Wichtige Credentials:
- **Keycloak Admin:** admin / admin
- **Realm:** hitec-realm
- **Client ID:** project-management-service
- **Client Secret:** [siehe Keycloak Console - Tab "Credentials"]
- **Required Role:** hitec-employee

### IntelliJ HTTP Client (einfachster Weg):
1. **Datei öffnen:** `src/main/ProjectRequests.http`
2. **Client Secret eintragen** (Zeile 13 - nur einmal!)
3. **Token holen:** Erster Request → Play
4. **API nutzen:** Beliebigen Request → Play

**Token wird automatisch wiederverwendet!** 🎉

---

## 11. Zusammenfassung - Der komplette Workflow

### Für IntelliJ-Nutzer (EMPFOHLEN):

1. ✅ **Docker starten:** `docker-compose up`
2. ✅ **Keycloak öffnen:** http://localhost:9090 (admin/admin)
3. ✅ **Client Secret kopieren:** 
   - Clients → project-management-service → Tab "Credentials" → Secret kopieren
4. ✅ **Secret eintragen:**
   - `src/main/ProjectRequests.http` öffnen
   - Zeile 13: `&client_secret=HIER-EINFÜGEN`
5. ✅ **Spring Boot starten:** Lf8StarterApplication
6. ✅ **Token holen:**
   - Erster Request in ProjectRequests.http
   - Auf ▶️ Play klicken
7. ✅ **API nutzen:**
   - Beliebigen Request auswählen
   - Auf ▶️ Play klicken
   - **Fertig!** 🎉

### Für Postman-Nutzer:

1. ✅ **Docker starten:** `docker-compose up`
2. ✅ **Keycloak konfigurieren:** http://localhost:9090 (admin/admin)
3. ✅ **Client überprüfen:** project-management-service mit hitec-employee Rolle
4. ✅ **Spring Boot starten:** Lf8StarterApplication
5. ✅ **Token holen:** POST zu `http://localhost:9090/realms/hitec-realm/protocol/openid-connect/token`
6. ✅ **API nutzen:** GET /projects mit `Authorization: Bearer [TOKEN]`

**Viel Erfolg! 🚀**

