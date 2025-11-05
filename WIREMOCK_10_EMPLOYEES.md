# ✅ WireMock - 10 Realistische Mitarbeiter erstellt!

## 📋 Übersicht aller Mitarbeiter (ID 1-10)

| ID | Name | Email | Qualifikationen |
|----|------|-------|-----------------|
| 1 | Max Mustermann | max.mustermann@hitec.de | Java Senior Developer, Scrum Master |
| 2 | Anna Schmidt | anna.schmidt@hitec.de | Python Expert, Project Manager |
| 3 | Thomas Müller | thomas.mueller@hitec.de | DevOps Engineer, Cloud Architect |
| 4 | Sarah Weber | sarah.weber@hitec.de | Frontend Developer, UX Designer |
| 5 | Michael Fischer | michael.fischer@hitec.de | Database Administrator, SQL Developer |
| 6 | Julia Wagner | julia.wagner@hitec.de | Java Senior Developer, Agile Coach |
| 7 | Daniel Becker | daniel.becker@hitec.de | Security Specialist, Network Administrator |
| 8 | Laura Hoffmann | laura.hoffmann@hitec.de | Project Manager, Business Analyst |
| 9 | Peter Schröder | peter.schroeder@hitec.de | Full Stack Developer, Scrum Master |
| 10 | Lisa Zimmermann | lisa.zimmermann@hitec.de | Data Scientist, Machine Learning Engineer |

## 🎯 Details pro Mitarbeiter

### Employee 1 - Max Mustermann
```json
GET /employees/1
{
  "employeeId": 1,
  "firstName": "Max",
  "lastName": "Mustermann",
  "email": "max.mustermann@hitec.de",
  "status": "ACTIVE"
}
```

**Qualifikationen**:
- Java Senior Developer (SENIOR) - gültig bis 2026-12-31
- Scrum Master (PROFESSIONAL) - gültig bis 2026-06-30

---

### Employee 2 - Anna Schmidt
```json
GET /employees/2
{
  "employeeId": 2,
  "firstName": "Anna",
  "lastName": "Schmidt",
  "email": "anna.schmidt@hitec.de",
  "status": "ACTIVE"
}
```

**Qualifikationen**:
- Python Expert (EXPERT) - gültig bis 2027-03-31
- Project Manager (SENIOR) - gültig bis 2026-09-30

---

### Employee 3 - Thomas Müller
```json
GET /employees/3
{
  "employeeId": 3,
  "firstName": "Thomas",
  "lastName": "Müller",
  "email": "thomas.mueller@hitec.de",
  "status": "ACTIVE"
}
```

**Qualifikationen**:
- DevOps Engineer (SENIOR) - gültig bis 2026-08-15
- Cloud Architect (PROFESSIONAL) - gültig bis 2027-01-31

---

### Employee 4 - Sarah Weber
**Qualifikationen**:
- Frontend Developer (SENIOR) - gültig bis 2026-11-30
- UX Designer (PROFESSIONAL) - gültig bis 2026-07-15

---

### Employee 5 - Michael Fischer
**Qualifikationen**:
- Database Administrator (EXPERT) - gültig bis 2027-02-28
- SQL Developer (SENIOR) - gültig bis 2026-10-31

---

### Employee 6 - Julia Wagner
**Qualifikationen**:
- Java Senior Developer (SENIOR) - gültig bis 2026-12-31
- Agile Coach (PROFESSIONAL) - gültig bis 2027-04-30

---

### Employee 7 - Daniel Becker
**Qualifikationen**:
- Security Specialist (EXPERT) - gültig bis 2026-09-30
- Network Administrator (SENIOR) - gültig bis 2027-01-15

---

### Employee 8 - Laura Hoffmann
**Qualifikationen**:
- Project Manager (SENIOR) - gültig bis 2027-05-31
- Business Analyst (PROFESSIONAL) - gültig bis 2026-08-31

---

### Employee 9 - Peter Schröder
**Qualifikationen**:
- Full Stack Developer (SENIOR) - gültig bis 2026-12-31
- Scrum Master (PROFESSIONAL) - gültig bis 2027-03-31

---

### Employee 10 - Lisa Zimmermann
**Qualifikationen**:
- Data Scientist (EXPERT) - gültig bis 2027-06-30
- Machine Learning Engineer (SENIOR) - gültig bis 2026-11-30

---

## 🧪 Test-Szenarien

### ✅ Erfolgreiche Mitarbeiter-Zuweisung

```http
POST /projects/1/employees
{
  "employeeId": 1,
  "qualification": "Java Senior Developer"
}
```
**Ergebnis**: ✅ 200 OK - Mitarbeiter 1 (Max Mustermann) wird zugewiesen

```http
POST /projects/1/employees
{
  "employeeId": 6,
  "qualification": "Java Senior Developer"
}
```
**Ergebnis**: ✅ 200 OK - Mitarbeiter 6 (Julia Wagner) wird zugewiesen

### ❌ Fehlerfall: Qualifikation nicht vorhanden

```http
POST /projects/1/employees
{
  "employeeId": 2,
  "qualification": "Java Senior Developer"
}
```
**Ergebnis**: ❌ 422 Unprocessable Entity - Anna Schmidt hat keine Java-Qualifikation (nur Python)

### 🔄 Verschiedene Mitarbeiter, gleiche Qualifikation

**Java Senior Developer** haben:
- Employee 1 (Max Mustermann)
- Employee 6 (Julia Wagner)

**Project Manager** haben:
- Employee 2 (Anna Schmidt)
- Employee 8 (Laura Hoffmann)

**Scrum Master** haben:
- Employee 1 (Max Mustermann)
- Employee 9 (Peter Schröder)

## 🎯 Fallback für unbekannte IDs

Für alle IDs **außer 1-10**:

```json
GET /employees/999
{
  "employeeId": 999,
  "firstName": "Unbekannt",
  "lastName": "Mitarbeiter",
  "email": "unknown@hitec.de",
  "status": "ACTIVE"
}
```

**Qualifikation**: "General Skills" (PROFESSIONAL)

## 📁 Erstellte Dateien

**Employee Mappings** (10 Stück):
- `wiremock/mappings/get-employee.json` (ID 1)
- `wiremock/mappings/get-employee-2.json` bis `-10.json`
- `wiremock/mappings/get-employee-fallback.json`

**Qualifications Mappings** (10 Stück):
- `wiremock/mappings/get-qualifications.json` (ID 1)
- `wiremock/mappings/get-qualifications-2.json` bis `-10.json`
- `wiremock/mappings/get-qualifications-fallback.json`

**Gesamt**: 22 Mapping-Dateien

## ✅ Tests bestätigt

```bash
# Employee 1
curl http://localhost:7070/employees/1
→ Max Mustermann ✅

# Employee 5
curl http://localhost:7070/employees/5
→ Michael Fischer ✅

# Employee 10
curl http://localhost:7070/employees/10
→ Lisa Zimmermann ✅

# Qualifications
curl http://localhost:7070/employees/3/qualifications
→ DevOps Engineer, Cloud Architect ✅
```

## 🚀 Vorteile

✅ **Realistisch**: Jeder Mitarbeiter hat eigene Daten
✅ **Testbar**: Verschiedene Qualifikationen für verschiedene Szenarien
✅ **Demo-Ready**: Perfekt für Präsentationen
✅ **Fehlerfrei**: Alle Mitarbeiter sind ACTIVE und haben gültige Qualifikationen
✅ **Flexibel**: Fallback für unbekannte IDs

## 📝 Verwendung in Requests

```http
# Projekt mit verantwortlichem Mitarbeiter erstellen
POST /projects
{
  "designation": "Web Development Project",
  "responsibleEmployeeId": 4,  // Sarah Weber (Frontend Developer)
  "customerId": 1,
  ...
}

# Mitarbeiter zu Projekt zuweisen
POST /projects/1/employees
{
  "employeeId": 7,  // Daniel Becker
  "qualification": "Security Specialist"
}
```

**Alles bereit für realistische Tests!** 🎉

