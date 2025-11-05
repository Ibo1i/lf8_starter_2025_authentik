# ✅ WireMock - 10 Realistic Employees Created!

## 📋 Overview of All Employees (ID 1-10)

| ID | Name | Email | Qualifications |
|----|------|-------|----------------|
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

## 🎯 Details per Employee

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

**Qualifications**:
- Java Senior Developer (SENIOR) - valid until 2026-12-31
- Scrum Master (PROFESSIONAL) - valid until 2026-06-30

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

**Qualifications**:
- Python Expert (EXPERT) - valid until 2027-03-31
- Project Manager (SENIOR) - valid until 2026-09-30

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

**Qualifications**:
- DevOps Engineer (SENIOR) - valid until 2026-08-15
- Cloud Architect (PROFESSIONAL) - valid until 2027-01-31

---

### Employee 4 - Sarah Weber
```json
GET /employees/4
{
  "employeeId": 4,
  "firstName": "Sarah",
  "lastName": "Weber",
  "email": "sarah.weber@hitec.de",
  "status": "ACTIVE"
}
```

**Qualifications**:
- Frontend Developer (SENIOR) - valid until 2026-11-30
- UX Designer (PROFESSIONAL) - valid until 2026-07-15

---

### Employee 5 - Michael Fischer
```json
GET /employees/5
{
  "employeeId": 5,
  "firstName": "Michael",
  "lastName": "Fischer",
  "email": "michael.fischer@hitec.de",
  "status": "ACTIVE"
}
```

**Qualifications**:
- Database Administrator (EXPERT) - valid until 2027-02-28
- SQL Developer (SENIOR) - valid until 2026-10-31

---

### Employee 6 - Julia Wagner
```json
GET /employees/6
{
  "employeeId": 6,
  "firstName": "Julia",
  "lastName": "Wagner",
  "email": "julia.wagner@hitec.de",
  "status": "ACTIVE"
}
```

**Qualifications**:
- Java Senior Developer (SENIOR) - valid until 2026-12-31
- Agile Coach (PROFESSIONAL) - valid until 2027-04-30

---

### Employee 7 - Daniel Becker
```json
GET /employees/7
{
  "employeeId": 7,
  "firstName": "Daniel",
  "lastName": "Becker",
  "email": "daniel.becker@hitec.de",
  "status": "ACTIVE"
}
```

**Qualifications**:
- Security Specialist (EXPERT) - valid until 2026-09-30
- Network Administrator (SENIOR) - valid until 2027-01-15

---

### Employee 8 - Laura Hoffmann
```json
GET /employees/8
{
  "employeeId": 8,
  "firstName": "Laura",
  "lastName": "Hoffmann",
  "email": "laura.hoffmann@hitec.de",
  "status": "ACTIVE"
}
```

**Qualifications**:
- Project Manager (SENIOR) - valid until 2027-05-31
- Business Analyst (PROFESSIONAL) - valid until 2026-08-30

---

### Employee 9 - Peter Schröder
```json
GET /employees/9
{
  "employeeId": 9,
  "firstName": "Peter",
  "lastName": "Schröder",
  "email": "peter.schroeder@hitec.de",
  "status": "ACTIVE"
}
```

**Qualifications**:
- Full Stack Developer (SENIOR) - valid until 2026-12-15
- Scrum Master (PROFESSIONAL) - valid until 2027-03-31

---

### Employee 10 - Lisa Zimmermann
```json
GET /employees/10
{
  "employeeId": 10,
  "firstName": "Lisa",
  "lastName": "Zimmermann",
  "email": "lisa.zimmermann@hitec.de",
  "status": "ACTIVE"
}
```

**Qualifications**:
- Data Scientist (EXPERT) - valid until 2027-06-30
- Machine Learning Engineer (SENIOR) - valid until 2026-11-30

---

## 🔧 Usage

### Get Employee Details
```bash
curl http://localhost:8081/employees/1
```

### Get Employee Qualifications
```bash
curl http://localhost:8081/employees/1/qualifications
```

### In Your Application
```java
// Employee Service will automatically use WireMock
employeeService.getEmployee(1L);
employeeService.getQualifications(1L);
```

## 📂 WireMock Configuration

Mock files are located in:
- **Mappings**: `wiremock/mappings/get-employee-*.json`
- **Qualifications**: `wiremock/mappings/get-qualifications-*.json`
- **Response Bodies**: `wiremock/__files/*.json`

## 🎯 Test Scenarios

### Successful Assignment
```http
POST /projects/1/employees
{
  "employeeId": 1,
  "qualification": "Java Senior Developer"
}
```
✅ Should work - Employee 1 has this qualification

### Missing Qualification
```http
POST /projects/1/employees
{
  "employeeId": 4,
  "qualification": "Java Senior Developer"
}
```
❌ Should fail - Employee 4 doesn't have Java skills

### Not Found
```http
POST /projects/1/employees
{
  "employeeId": 999,
  "qualification": "Any"
}
```
❌ Should return 404 - Employee doesn't exist

## 🔄 Circuit Breaker Testing

The application includes a Circuit Breaker for the Employee Service.

### Trigger Circuit Breaker
1. Make 5 requests to non-existent employee (ID 999)
2. Circuit Breaker opens after 5 failures
3. Next requests fail immediately with 503
4. After 60 seconds, Circuit Breaker transitions to half-open
5. One successful request closes the Circuit Breaker

### Monitor Circuit Breaker
```bash
curl http://localhost:8080/actuator/circuitbreakers
curl http://localhost:8080/actuator/circuitbreakerevents
```

## 🚀 Quick Test

Test all employees with valid qualifications:

```bash
# Employee 1 - Java Developer
curl -X POST http://localhost:8080/projects/1/employees \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"employeeId": 1, "qualification": "Java Senior Developer"}'

# Employee 2 - Python Expert  
curl -X POST http://localhost:8080/projects/1/employees \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"employeeId": 2, "qualification": "Python Expert"}'

# Employee 3 - DevOps Engineer
curl -X POST http://localhost:8080/projects/1/employees \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"employeeId": 3, "qualification": "DevOps Engineer"}'
```

## 📊 Statistics

- **Total Employees**: 10
- **Total Qualifications**: 20 (2 per employee)
- **Qualification Levels**: SENIOR, PROFESSIONAL, EXPERT
- **All Valid Until**: 2026-2027
- **Mock Endpoints**: 20 (10 employees + 10 qualifications)

## 🛠️ Customization

To add more employees or modify existing ones:

1. Create new mapping file: `wiremock/mappings/get-employee-11.json`
2. Create qualification mapping: `wiremock/mappings/get-qualifications-11.json`
3. Restart WireMock: `docker compose restart`

**Template:**
```json
{
  "request": {
    "method": "GET",
    "urlPathPattern": "/employees/11"
  },
  "response": {
    "status": 200,
    "jsonBody": {
      "employeeId": 11,
      "firstName": "New",
      "lastName": "Employee",
      "email": "new.employee@hitec.de",
      "status": "ACTIVE"
    }
  }
}
```

---

**✅ All 10 employees ready for testing!**

