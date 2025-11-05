# Keycloak JWT Authentication - Complete Guide

## Overview
This guide walks you step-by-step through setting up Keycloak and using JWT tokens for authenticated API requests.

---

## ⚡ Quick Start for IntelliJ Users

**Good news!** All API requests are already prepared in:
📁 **`src/main/ProjectRequests.http`**

**You only need to:**
1. ✅ Configure Keycloak (Chapter 2)
2. ✅ Enter Client Secret in the file (Chapter 4.2.2)
3. ✅ Execute Token Request (Chapter 4.2.3)
4. ✅ Use any API requests (Chapter 5.2)

**➡️ Jump directly to Chapter 2 if you're using IntelliJ!**

---

## 1. Start Docker Container

### 1.1 Ensure Docker Desktop is Running
Make sure Docker Desktop is running.

### 1.2 Start Keycloak Container
Open a terminal/PowerShell in the project directory and execute:

```bash
docker-compose up
```

**Expected output:**
```
[+] Running 3/3
 ✔ Network lf8_starter_2025_authentik_default  Created
 ✔ Container hitec-keycloak                    Started
 ✔ Container lf8Starter_postgres               Started
```

### 1.3 Check Container Status
```bash
docker-compose ps
```

**Expected output:**
```
NAME                IMAGE                           STATUS
hitec-keycloak      quay.io/keycloak/keycloak:23.0  Up .. seconds (healthy)
lf8Starter_postgres postgres:latest                 Up .. seconds
```

⚠️ **Important:** Wait until the status shows `Up (healthy)`!

---

## 2. Keycloak Admin Console Access

### 2.1 Open Keycloak
1. Open browser
2. Navigate to: http://localhost:9090
3. Click on **"Administration Console"**

### 2.2 Login
- **Username:** `admin`
- **Password:** `admin`

---

## 3. Verify Realm and Client

### 3.1 Check Realm
1. At the top left in the dropdown menu, **"hitec-realm"** should already exist
   
   **If the realm doesn't exist:**
   - Click on the dropdown menu at the top left
   - Select **"Create Realm"**
   - Name: `hitec-realm`
   - **Enabled:** ON
   - Click **"Create"**

### 3.2 Check Client

1. In the left menu, click **"Clients"**
2. Check if **"project-management-service"** exists

**If the client doesn't exist:**
1. Click **"Create client"**
2. **General Settings:**
   - Client type: `OpenID Connect`
   - Client ID: `project-management-service`
   - Name: `Project Management Service`
   - Click **"Next"**

3. **Capability config:**
   - ✅ Client authentication: **ON**
   - ✅ Service account roles: **ON**
   - Authorization: OFF
   - Click **"Next"**

4. **Login settings:**
   - Leave empty
   - Click **"Save"**

### 3.3 Get Client Secret

1. Click on the client **"project-management-service"**
2. Navigate to the **"Credentials"** tab
3. Copy the **Client Secret**
   
   📋 **IMPORTANT:** Note this secret!
   
   Example: `jBCaEJraTUdFX1CzbHvxGLrxZmGmggrF`

### 3.4 Assign Realm Role

1. Click on **"Service account roles"** tab
2. Click **"Assign role"**
3. **Filter:** Select `Filter by realm roles`
4. Search for **"hitec-employee"**
5. Select the checkbox for **"hitec-employee"**
6. Click **"Assign"**

✅ **Success!** Keycloak is now configured.

---

## 4. Get JWT Token

### 4.1 Variant A: Using Postman

#### 4.1.1 Create New Request
1. Open Postman
2. Click **"New"** → **"HTTP Request"**
3. Method: `POST`
4. URL: `http://localhost:9090/realms/hitec-realm/protocol/openid-connect/token`

#### 4.1.2 Set Body
Click on the tab **"Body"** and select:
- ☑️ `x-www-form-urlencoded`

Add the following key-value pairs:

| Key | Value |
|-----|-------|
| `client_id` | `project-management-service` |
| `client_secret` | `YOUR_CLIENT_SECRET_FROM_STEP_3.3` |
| `grant_type` | `client_credentials` |

#### 4.1.3 Set Headers
Click on the tab **"Headers"** and add:

| Key | Value |
|-----|-------|
| `Content-Type` | `application/x-www-form-urlencoded` |

#### 4.1.4 Send Request
Click **"Send"**

**Expected response:**
```json
{
  "access_token": "eyJhbGciOiJSUzI1NiIsInR5cCI...",
  "expires_in": 300,
  "token_type": "Bearer",
  "scope": "email profile"
}
```

📋 **Copy the `access_token`** - you'll need it for API calls!

---

### 4.2 Variant B: Using IntelliJ HTTP Client (RECOMMENDED)

The project already contains a **fully configured** file for all API requests!

1. Open the file: **`src/main/ProjectRequests.http`**

#### 4.2.1 File Structure
```http
### 1. Get JWT Token from Keycloak
POST http://localhost:9090/realms/hitec-realm/protocol/openid-connect/token
Content-Type: application/x-www-form-urlencoded

client_id=project-management-service
&client_secret=YOUR_CLIENT_SECRET_HERE
&grant_type=client_credentials

### 2. Create Project
POST http://localhost:8080/projects
Authorization: Bearer {{token}}
Content-Type: application/json

{
  "designation": "Cloud Migration Alpha",
  ...
}
```

#### 4.2.2 Enter Client Secret
1. Find the line: `client_secret=YOUR_CLIENT_SECRET_HERE`
2. Replace `YOUR_CLIENT_SECRET_HERE` with your actual Client Secret
3. Save the file

#### 4.2.3 Execute Token Request
1. Click on the green arrow ▶️ next to `### 1. Get JWT Token`
2. Wait for response
3. IntelliJ automatically saves the token!

**Response:**
```json
{
  "access_token": "eyJhbGciOiJSUzI1NiIsInR5cCI...",
  "expires_in": 300,
  "token_type": "Bearer"
}
```

✅ **Token is now automatically available for all subsequent requests!**

---

## 5. Use JWT Token for API Calls

### 5.1 With Postman

#### 5.1.1 Create New Request
1. New Request
2. Method: `GET`
3. URL: `http://localhost:8080/projects`

#### 5.1.2 Add Authorization Header
Navigate to tab **"Headers"**:

| Key | Value |
|-----|-------|
| `Authorization` | `Bearer YOUR_ACCESS_TOKEN_FROM_STEP_4` |

**Example:**
```
Authorization: Bearer eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9...
```

#### 5.1.3 Send Request
Click **"Send"**

**Expected response:**
```json
[
  {
    "id": 1,
    "designation": "Cloud Migration Alpha",
    ...
  }
]
```

---

### 5.2 With IntelliJ HTTP Client (RECOMMENDED)

**Advantage:** Token is automatically used!

1. Open: `src/main/ProjectRequests.http`
2. Scroll to any request (e.g., `### 2. Create Project`)
3. Click the green arrow ▶️
4. Done!

**All requests already have:**
```http
Authorization: Bearer {{token}}
```

✅ **The token from Step 4.2.3 is automatically inserted!**

---

## 6. Test All Endpoints

The `ProjectRequests.http` file contains examples for:

### 6.1 Project Management
```http
### 2. Create Project
POST http://localhost:8080/projects

### 3. Get All Projects
GET http://localhost:8080/projects

### 4. Get Project by ID
GET http://localhost:8080/projects/1

### 5. Update Project
PUT http://localhost:8080/projects/1

### 6. Delete Project
DELETE http://localhost:8080/projects/1
```

### 6.2 Employee Assignment
```http
### 7. Assign Employee to Project
POST http://localhost:8080/projects/1/employees

### 8. Get Project Employees
GET http://localhost:8080/projects/1/employees

### 9. Remove Employee from Project
DELETE http://localhost:8080/projects/1/employees/2
```

### 6.3 Employee Projects
```http
### 10. Get Projects for Employee
GET http://localhost:8080/employees/2/projects
```

---

## 7. Troubleshooting

### 7.1 Error: "401 Unauthorized"
**Cause:** Token is invalid or expired

**Solution:**
1. Execute Token Request again (Step 4)
2. Tokens are valid for 5 minutes (300 seconds)
3. Check if Keycloak is running

### 7.2 Error: "403 Forbidden"
**Cause:** Missing role `hitec-employee`

**Solution:**
1. Go to Keycloak Admin Console
2. Clients → project-management-service
3. Service account roles tab
4. Assign **hitec-employee** role

### 7.3 Error: "Connection refused"
**Cause:** Keycloak or Application not running

**Solution:**
```bash
# Check Docker containers
docker-compose ps

# Start if not running
docker-compose up

# Start Spring Boot application
.\gradlew.bat bootRun
```

### 7.4 Token doesn't work in IntelliJ
**Cause:** Token wasn't saved

**Solution:**
1. Execute Token Request **first**
2. Wait for successful response
3. Then execute API requests
4. IntelliJ automatically uses the token

---

## 8. Alternative: cURL

### 8.1 Get Token
```bash
curl -X POST http://localhost:9090/realms/hitec-realm/protocol/openid-connect/token \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "client_id=project-management-service" \
  -d "client_secret=YOUR_CLIENT_SECRET" \
  -d "grant_type=client_credentials"
```

### 8.2 Use Token
```bash
curl -X GET http://localhost:8080/projects \
  -H "Authorization: Bearer YOUR_ACCESS_TOKEN"
```

---

## 9. Configuration Details

### 9.1 application.yml
```yaml
spring:
  security:
    oauth2:
      resourceserver:
        jwt:
          issuer-uri: http://localhost:9090/realms/hitec-realm
          jwk-set-uri: http://localhost:9090/realms/hitec-realm/protocol/openid-connect/certs

keycloak:
  enabled: true
  realm: hitec-realm
  client-id: project-management-service
  required-role: hitec-employee
```

### 9.2 Token Structure
```json
{
  "exp": 1234567890,
  "iat": 1234567590,
  "jti": "...",
  "iss": "http://localhost:9090/realms/hitec-realm",
  "sub": "...",
  "typ": "Bearer",
  "azp": "project-management-service",
  "realm_access": {
    "roles": [
      "hitec-employee"
    ]
  }
}
```

---

## 10. Summary

✅ **You have learned:**
1. How to start Keycloak with Docker
2. How to configure Realm and Client
3. How to get JWT tokens
4. How to use tokens for API calls
5. How to use IntelliJ HTTP Client effectively

**🎉 Ready for authenticated API development!**

---

## Additional Resources

- [Keycloak Documentation](https://www.keycloak.org/documentation)
- [JWT.io Decoder](https://jwt.io/) - Decode and inspect tokens
- [Spring Security OAuth2](https://spring.io/projects/spring-security-oauth)

