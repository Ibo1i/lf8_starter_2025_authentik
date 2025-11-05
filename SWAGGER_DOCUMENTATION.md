# Swagger/OpenAPI Documentation Guide

## Overview

The HiTec Project Management API is fully documented using OpenAPI 3.0 with an interactive Swagger UI interface.

## Accessing the Documentation

### Swagger UI (Interactive)
- **URL:** `http://localhost:8080/swagger-ui/index.html`
- **Description:** Interactive web interface to explore and test API endpoints
- **Features:**
  - Try out endpoints directly from the browser
  - View all request/response schemas
  - See example payloads for all operations
  - Execute authenticated requests using JWT tokens

### OpenAPI JSON Specification
- **URL:** `http://localhost:8080/v3/api-docs`
- **Description:** Machine-readable OpenAPI 3.0 specification in JSON format
- **Usage:** Can be imported into tools like Postman, Insomnia, or API testing frameworks

## Authentication in Swagger UI

### Step 1: Obtain a JWT Token
You need a valid JWT token from Keycloak or Authentik. See `KEYCLOAK_SETUP_ANLEITUNG.md` for details.

Example using the provided HTTP file:
1. Open `GetBearerToken.http`
2. Execute the token request
3. Copy the `access_token` from the response

### Step 2: Authorize in Swagger UI
1. Open Swagger UI at `http://localhost:8080/swagger-ui/index.html`
2. Click the **"Authorize"** button (lock icon) in the top right
3. Enter your JWT token in the format: `Bearer <your-token-here>`
   - Example: `Bearer eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9...`
4. Click **"Authorize"**
5. Click **"Close"**

### Step 3: Execute Requests
- All endpoints are now authenticated
- Click "Try it out" on any endpoint
- Fill in the required parameters
- Click "Execute"
- View the response directly in the browser

## API Structure

The API is organized into the following tags:

### 1. Projects
Main project management operations:
- `POST /projects` - Create a new project
- `GET /projects` - Get all projects
- `GET /projects/{projectId}` - Get project by ID
- `PUT /projects/{projectId}` - Update a project
- `DELETE /projects/{projectId}` - Delete a project
- `POST /projects/{projectId}/employees` - Assign employee to project
- `DELETE /projects/{projectId}/employees/{employeeId}` - Remove employee from project
- `GET /projects/{projectId}/employees` - Get all employees of a project

### 2. Employee-Projects
Employee-centric views:
- `GET /employees/{employeeId}/projects` - Get all projects for a specific employee

## Response Codes

All endpoints document the following HTTP status codes where applicable:

### Success Codes
- **200 OK** - Request successful, data returned
- **201 Created** - Resource successfully created
- **204 No Content** - Resource successfully deleted

### Client Error Codes
- **400 Bad Request** - Validation failed (see `validationErrors` in response)
- **401 Unauthorized** - Missing or invalid JWT token
- **403 Forbidden** - Insufficient permissions
- **404 Not Found** - Resource not found
- **409 Conflict** - Business rule violation (time conflict, duplicate assignment)
- **422 Unprocessable Entity** - Missing or expired qualification

### Server/Integration Error Codes
- **502 Bad Gateway** - Employee Service returned an error
- **503 Service Unavailable** - Circuit Breaker is OPEN
- **504 Gateway Timeout** - Employee Service timeout

## Error Response Structure

All errors follow a standardized format:

```json
{
  "timestamp": "2025-01-15T14:30:00",
  "status": 409,
  "error": "Conflict",
  "message": "Employee is already assigned to another project during this time period",
  "path": "/projects/1001/employees",
  "conflictingProjects": [
    {
      "projectId": 1002,
      "projectName": "Database Optimization Project",
      "startDate": "2025-02-01",
      "endDate": "2025-05-31"
    }
  ]
}
```

### Error Response Fields
- `timestamp` - When the error occurred
- `status` - HTTP status code
- `error` - HTTP status reason phrase
- `message` - Human-readable error description
- `path` - The request path that caused the error
- `validationErrors` - List of field validation errors (400 only)
- `conflictingProjects` - Details about conflicting projects (409 only)
- `service` - Name of the failed external service (502/503/504 only)
- `upstreamStatus` - HTTP status from upstream service (502 only)
- `circuitBreakerState` - Current circuit breaker state (503 only)
- `retryAfter` - Seconds until service might be available (503 only)

## Example Requests

### Create a Project

**Request:**
```json
POST /projects
{
  "designation": "Cloud Migration Project Alpha",
  "responsibleEmployeeId": 1,
  "customerId": 42,
  "customerContactPerson": "John Miller",
  "comment": "Migration of legacy systems to AWS infrastructure",
  "startDate": "2025-01-15",
  "plannedEndDate": "2025-06-30",
  "actualEndDate": null
}
```

**Response (201 Created):**
```json
{
  "id": 1001,
  "designation": "Cloud Migration Project Alpha",
  "responsibleEmployeeId": 1,
  "customerId": 42,
  "customerContactPerson": "John Miller",
  "comment": "Migration of legacy systems to AWS infrastructure",
  "startDate": "2025-01-15",
  "plannedEndDate": "2025-06-30",
  "actualEndDate": null,
  "employeeIds": []
}
```

### Assign Employee to Project

**Request:**
```json
POST /projects/1001/employees
{
  "employeeId": 2,
  "qualification": "Java Developer"
}
```

**Success Response (200 OK):**
```json
{
  "projectId": 1001,
  "projectName": "Cloud Migration Project Alpha",
  "employeeId": 2,
  "employeeName": "Jane Doe"
}
```

**Error Response - Missing Qualification (422):**
```json
{
  "timestamp": "2025-01-15T14:30:00",
  "status": 422,
  "error": "Unprocessable Entity",
  "message": "Employee does not have the required qualification: Java Developer",
  "path": "/projects/1001/employees"
}
```

**Error Response - Time Conflict (409):**
```json
{
  "timestamp": "2025-01-15T14:30:00",
  "status": 409,
  "error": "Conflict",
  "message": "Employee 2 is already assigned to another project during this time period",
  "path": "/projects/1001/employees",
  "conflictingProjects": [
    {
      "projectId": 1002,
      "projectName": "Database Optimization Project",
      "startDate": "2025-02-01",
      "endDate": "2025-05-31"
    }
  ]
}
```

## Testing with WireMock

When testing with WireMock (Employee Service mock):

1. Start the application (it includes WireMock)
2. Use employee IDs 1-10 for successful responses
3. Use employee ID 999 to trigger a 404 Not Found
4. Circuit breaker will open after 5 consecutive failures

See `WIREMOCK_10_EMPLOYEES.md` for more details.

## Configuration

OpenAPI configuration is located in:
- **Java Config:** `src/main/java/de/szut/lf8_starter/config/OpenAPIConfiguration.java`
- **Application Config:** `src/main/resources/application.yml` (springdoc section)

### Customization Options

In `application.yml`:
```yaml
springdoc:
  swagger-ui:
    path: /swagger-ui/index.html           # Swagger UI path
    try-it-out-enabled: true                # Enable "Try it out" feature
    tags-sorter: alpha                      # Sort tags alphabetically
    operations-sorter: alpha                # Sort operations alphabetically
    display-request-duration: true          # Show request execution time
  api-docs:
    path: /v3/api-docs                      # OpenAPI JSON path
    enabled: true                           # Enable API docs endpoint
```

## Best Practices

1. **Always authenticate** before testing endpoints in Swagger UI
2. **Check example values** in the schema documentation for realistic test data
3. **Read endpoint descriptions** for business rule details
4. **Review error examples** to understand failure scenarios
5. **Use the Circuit Breaker health endpoint** (`/actuator/circuitbreakerevents`) to monitor Employee Service integration

## Additional Resources

- [OpenAPI Specification](https://spec.openapis.org/oas/v3.0.3)
- [Swagger UI Documentation](https://swagger.io/tools/swagger-ui/)
- [SpringDoc OpenAPI](https://springdoc.org/)

## Support

For issues or questions regarding the API documentation, please contact the HiTec Development Team.

