# LF08 Project Starter - Project Management Service

This project implements a **Project Management Service** with the following features:
- ✅ JWT Authentication (Keycloak)
- ✅ Employee Service Integration with Circuit Breaker
- ✅ PostgreSQL Database
- ✅ RESTful API with OpenAPI/Swagger
- ✅ Resilience4j for Fault Tolerance

## 🚀 Quick Start

### Prerequisites
* Docker: https://docs.docker.com/get-docker/
* Docker Compose (included with Docker Desktop on Windows and Mac): https://docs.docker.com/compose/install/

### Start All Services

```bash
docker compose up
```

**This automatically starts:**
1. ✅ PostgreSQL Database (Port 5432)
2. ✅ Keycloak (Port 9090)
3. ✅ Employee Service Mock (WireMock on Port 8081)

**⏱️ Wait time:** ~60 seconds until Keycloak is fully started.

### Start the Application

**Option A: With Gradle (local development)**
```bash
# Windows
.\gradlew.bat bootRun

# Linux/Mac
./gradlew bootRun
```

**Option B: With Docker (like in production)**
```bash
# TODO: Create Dockerfile if desired
```

## 📍 Important Endpoints

| Service | URL | Description |
|---------|-----|-------------|
| **API** | http://localhost:8080 | Project Management Service |
| **Swagger UI** | http://localhost:8080/swagger-ui/index.html | API Documentation |
| **Keycloak Admin** | http://localhost:9090 | Admin: `admin` / `admin` |
| **Employee Service Mock** | http://localhost:8081 | WireMock Mock Service |
| **PostgreSQL** | localhost:5432 | DB: `lf8Starter`, User: `user`, PW: `secret` |

## 🔐 Authentication

### Get JWT Token

1. Open file: `GetBearerToken.http`
2. Execute request (green arrow in IntelliJ)
3. Copy `access_token` from response
4. Insert token in further requests: `Authorization: Bearer <token>`

**Or directly via cURL:**
```bash
curl -X POST http://localhost:9090/realms/hitec-realm/protocol/openid-connect/token \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "client_id=project-management-service" \
  -d "client_secret=jBCaEJraTUdFX1CzbHvxGLrxZmGmggrF" \
  -d "grant_type=client_credentials"
```

## 🧪 API Testing

### With HTTP Files (IntelliJ)

All requests prepared in: `src/main/ProjectRequests.http`

**Workflow:**
1. Get token (see above)
2. Create project: `POST /projects`
3. Assign employee: `POST /projects/{id}/employees`

### With Swagger UI

1. Open http://localhost:8080/swagger-ui/index.html
2. Click "Authorize"
3. Insert token: `Bearer <your-token>`
4. Execute requests

## 👨‍💼 Employee Service Mock

The Employee Service is simulated through **WireMock**.

### Available Mock Endpoints

```
GET /employees/E-{id}
→ Returns employee details

GET /employees/E-{id}/qualifications  
→ Returns qualifications
```

### Example Responses

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

### Customize Mock Configuration

Mock data is located in:
- `wiremock/mappings/*.json` - Request/Response Mappings
- `wiremock/__files/*.json` - Response Bodies (optional)

## 🗄️ Database

### Start Services
```bash
docker compose up
```
**Note:** Containers run permanently! Stop when not needed.

### Stop Services
```bash
docker compose down
```

### Reset Database (in case of problems)
```bash
docker compose down
docker volume rm lf8_starter_2025_authentik_lf8Starter_postgres_data
docker compose up
```

### Set up PostgreSQL in IntelliJ

1. Start Docker container with PostgreSQL
2. Open `src/main/resources/application.yml` and copy DB URL
3. On the right: Open **Database** tab
4. Click on database symbol with key
5. **+** → **Data Source from URL**
6. Paste URL: `jdbc:postgresql://localhost:5432/lf8Starter`
7. Select PostgreSQL driver → **OK**
8. Username: `user`, Password: `secret` → **Apply**
9. **Schemas** tab: Only activate `lf8_starter_db` and `public`
10. **Apply** → **OK**

## 🔧 Development

### Build Project
```bash
# Windows
.\gradlew.bat build

# Linux/Mac
./gradlew build
```

### Run Tests
```bash
# All tests
.\gradlew.bat test

# Specific test
.\gradlew.bat test --tests "*CircuitBreakerIntegrationTest"
```

### Code Structure

```
src/
├── main/
│   ├── java/de/szut/lf8_starter/
│   │   ├── project/              # Project Management Domain
│   │   ├── integration/employee/ # Employee Service Integration
│   │   ├── security/             # JWT & Keycloak Config
│   │   ├── exceptionHandling/    # Global Exception Handling
│   │   └── config/               # Spring Configuration
│   └── resources/
│       ├── application.yml       # Main Configuration
│       └── application.properties
└── test/
    └── java/de/szut/lf8_starter/
        ├── integration/          # Integration Tests
        ├── unittest/             # Unit Tests
        └── hello/                # Example Tests
```

## 📚 Documentation

- **Swagger UI**: http://localhost:8080/swagger-ui/index.html
- **OpenAPI JSON**: http://localhost:8080/v3/api-docs
- **Keycloak Setup**: See `KEYCLOAK_SETUP_GUIDE.md`
- **WireMock Guide**: See `WIREMOCK_10_EMPLOYEES.md`

## 🛠️ Technologies

- **Java 22**
- **Spring Boot 3.3.4**
- **Spring Security** with JWT
- **Spring Data JPA**
- **PostgreSQL**
- **Keycloak** for Authentication
- **Resilience4j** for Circuit Breaker
- **OpenFeign** for HTTP Clients
- **WireMock** for Testing
- **SpringDoc OpenAPI** for API Documentation

## 🔒 Security

All endpoints require JWT authentication except:
- `/welcome` - Health check
- `/swagger-ui/**` - API Documentation
- `/v3/api-docs/**` - OpenAPI Specification

**Required Role:** `hitec-employee`

## 🚨 Troubleshooting

### Keycloak doesn't start
```bash
docker compose down
docker compose up
# Wait for "Keycloak started"
```

### Database connection error
```bash
# Check if PostgreSQL is running
docker ps | findstr postgres

# Restart database
docker compose restart postgres
```

### Tests fail
```bash
# Clean and rebuild
.\gradlew.bat clean build

# Run specific test with debug output
.\gradlew.bat test --tests "*YourTest" --info
```

### Circuit Breaker always OPEN
- Check WireMock is running: http://localhost:8081/__admin/
- Verify employee service URL in `application.yml`
- Check circuit breaker settings in `application.yml`

## 📝 License

This project is for educational purposes (LF08 - Application Development).

## 👥 Contributors

Developed by students and teachers at SZ Utbremen.

## 🔗 Related Links

- [Spring Boot Documentation](https://spring.io/projects/spring-boot)
- [Keycloak Documentation](https://www.keycloak.org/documentation)
- [Resilience4j Guide](https://resilience4j.readme.io/)
- [WireMock Documentation](https://wiremock.org/docs/)

