# Lending Management System (LMS)

A Digital Lending Platform built with Spring Boot that integrates with Core Banking Systems (CBS) and Scoring Engines to provide automated micro-loan services for bank customers through multi-channel access.

## 🏗️ **System Architecture**

The LMS is designed as a middleware platform that bridges bank mobile applications with core banking systems and external scoring services, following clean architecture principles and SOLID design patterns.

### **High-Level Flow**
```
Mobile App (USSD/iOS/Android) → LMS APIs → CBS (KYC/Transactions) → Scoring Engine → Loan Decision
```

## 🔑 **Key Features**

- **Customer Subscription Management** - KYC validation and customer onboarding
- **Automated Loan Processing** - Real-time loan application processing with scoring
- **Credit Scoring Integration** - Two-step scoring process with retry mechanism
- **Transaction Data Integration** - SOAP-based transaction history retrieval
- **Multi-Channel Support** - REST APIs for mobile applications (USSD, iOS, Android)
- **Resilient Architecture** - Automatic retry logic and graceful error handling
- **Comprehensive Logging** - Structured logging for monitoring and debugging
- **Background Processing** - Scheduled jobs for loan scoring and status updates

## 💻 **Technology Stack**

- **Language**: Java 17
- **Framework**: Spring Boot 3.2.5
- **Build Tool**: Gradle
- **Database**: H2 (development), PostgreSQL (production)
- **Documentation**: OpenAPI 3 (Swagger)
- **Testing**: JUnit 5, Mockito, Spring Boot Test
- **Integration**: Spring WS (SOAP), REST
- **Scheduling**: Spring Scheduling

## 🛠️ **System Requirements**

### **Prerequisites**
- Java 17+
- Gradle 7.x+
- PostgreSQL 14+ (for production)
- Git

### **External Service Dependencies**
- Core Banking System (CBS) with KYC and Transaction APIs
- External Scoring Engine
- Bank Mobile Application

## 🚀 **Installation & Setup**

### **1. Clone the Repository**
```bash
git clone <repository-url>
cd lending-management-system
```

### **2. Build the Project**
```bash
./gradlew clean build
```

### **3. Run the Application**

#### **Development Mode (H2 Database)**
```bash
./gradlew bootRun
```

#### **Production Mode**
```bash
./gradlew bootJar
java -jar build/libs/lending-management-system-0.0.1-SNAPSHOT.jar --spring.profiles.active=prod
```

### **4. Access the Application**
- **Application**: `http://localhost:8080`
- **API Documentation**: `http://localhost:8080/swagger-ui.html`
- **Health Check**: `http://localhost:8080/api/v1/health`

## ⚙️ **Configuration**

### **Application Properties**
The application uses `application.properties` for configuration management:

```properties
# Server Configuration
server.port=8080
spring.application.name=lending-management-system

# Database Configuration (Development)
spring.datasource.url=jdbc:h2:mem:testdb
spring.datasource.driver-class-name=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=password

# Database Configuration (Production)
# spring.datasource.url=jdbc:postgresql://localhost:5432/lms
# spring.datasource.username=lms_user
# spring.datasource.password=your_password

# CBS SOAP Configuration
cbs.kyc.wsdl.url=https://kycapidevtest.credable.io/service/customerWsdl.wsdl
cbs.transaction.wsdl.url=https://trxapidevtest.credable.io/service/transactionWsdl.wsdl
cbs.username=admin
cbs.password=pwd123

# Scoring Engine Configuration
scoring.base.url=https://scoringdevtest.credable.io
scoring.initiate.path=/api/v1/scoring/initiateQueryScore
scoring.query.path=/api/v1/scoring/queryScore
scoring.client.create.path=/api/v1/client/createClient
scoring.retry.max.attempts=5
scoring.retry.delay.seconds=10

# Transaction Data API Configuration
transaction.api.service.name=LMS Transaction Service
transaction.api.username=lms_user
transaction.api.password=lms_pass_123

# Security Configuration
spring.security.user.name=admin
spring.security.user.password=admin123
spring.security.user.roles=ADMIN
```

### **Environment-Specific Configuration**

#### **Development (`application-dev.properties`)**
```properties
# H2 Database
spring.h2.console.enabled=true
logging.level.com.lendingplatform=DEBUG

# Mock external services for development
external.services.mock.enabled=true
```

#### **Production (`application-prod.properties`)**
```properties
# PostgreSQL Database
spring.datasource.url=${DB_URL:jdbc:postgresql://localhost:5432/lms}
spring.datasource.username=${DB_USERNAME:lms_user}
spring.datasource.password=${DB_PASSWORD:your_password}

# Production logging
logging.level.com.lendingplatform=INFO
logging.pattern.console=%d{yyyy-MM-dd HH:mm:ss} - %logger{36} - %level - %msg%n

# External service URLs
cbs.kyc.wsdl.url=${CBS_KYC_URL:https://kycapi.bank.com/service/customerWsdl.wsdl}
scoring.base.url=${SCORING_URL:https://scoring.credable.io}
```

## 📡 **API Endpoints**

### **Loan Management APIs**

#### **Customer Subscription**
```http
POST /api/v1/loans/subscribe
Content-Type: application/json
Authorization: Basic YWRtaW46YWRtaW4xMjM=

{
    "customerNumber": "234774784"
}
```

#### **Loan Request**
```http
POST /api/v1/loans/request
Content-Type: application/json
Authorization: Basic YWRtaW46YWRtaW4xMjM=

{
    "customerNumber": "234774784",
    "amount": 15000
}
```

#### **Loan Status**
```http
GET /api/v1/loans/status/{customerNumber}
Authorization: Basic YWRtaW46YWRtaW4xMjM=
```

### **Transaction Data API (For Scoring Service)**

#### **Get Transaction Data**
```http
GET /api/v1/transaction-data/{customerNumber}
Authorization: Basic bG1zX3VzZXI6bG1zX3Bhc3NfMTIz
```

### **System Health**
```http
GET /api/v1/health
```

## 🔐 **Authentication & Security**

### **User Accounts**
- **Admin User**: `admin:admin123` (ADMIN role)
- **Scoring Service User**: `lms_user:lms_pass_123` (USER role)

### **API Security**
- Basic Authentication for all endpoints
- Role-based access control
- Public access for health checks and API documentation

### **Security Headers**
```bash
# Admin Authentication
Authorization: Basic YWRtaW46YWRtaW4xMjM=

# Scoring Service Authentication
Authorization: Basic bG1zX3VzZXI6bG1zX3Bhc3NfMTIz
```

## 🔄 **Business Logic & Workflow**

### **1. Customer Subscription Flow**
1. Mobile app submits customer number
2. LMS validates customer via CBS KYC API (SOAP)
3. Customer details stored locally
4. Subscription confirmation returned

### **2. Loan Application Flow**
1. Mobile app submits loan request (customer + amount)
2. LMS validates customer subscription
3. Checks for existing ongoing loans
4. Creates loan record with PENDING status
5. Initiates scoring with external scoring engine
6. Updates loan status to SCORING_IN_PROGRESS
7. Returns loan application confirmation

### **3. Background Scoring Process**
1. Scheduled job runs every 30 seconds
2. Queries scoring engine for results using token
3. Implements retry logic (max 5 attempts)
4. Updates loan status based on scoring results
5. Applies business rules for approval/rejection

### **4. Loan Decision Criteria**
**Approved if:**
- Credit Score ≥ 500
- No exclusions flagged
- Requested amount ≤ Credit limit

**Rejected otherwise**

## 🗄️ **Database Schema**

### **Core Entities**

#### **Customer Table**
```sql
CREATE TABLE customers (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    customer_number VARCHAR(255) UNIQUE NOT NULL,
    first_name VARCHAR(255),
    last_name VARCHAR(255),
    phone_number VARCHAR(255),
    email VARCHAR(255),
    national_id VARCHAR(255),
    is_active BOOLEAN,
    registration_date TIMESTAMP,
    scoring_token VARCHAR(255),
    created_at TIMESTAMP,
    updated_at TIMESTAMP
);
```

#### **Loan Table**
```sql
CREATE TABLE loans (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    customer_number VARCHAR(255) NOT NULL,
    requested_amount DECIMAL(19,2) NOT NULL,
    approved_amount DECIMAL(19,2),
    status ENUM('PENDING', 'SCORING_IN_PROGRESS', 'APPROVED', 'REJECTED', 'FAILED') NOT NULL,
    credit_score INTEGER,
    credit_limit DECIMAL(19,2),
    exclusion VARCHAR(255),
    exclusion_reason VARCHAR(255),
    application_date TIMESTAMP,
    approval_date TIMESTAMP,
    retry_count INTEGER DEFAULT 0,
    created_at TIMESTAMP,
    updated_at TIMESTAMP
);
```

## 🧪 **Testing**

### **Test Categories**
- **Unit Tests**: Service layer with mocked dependencies
- **Integration Tests**: Full application context with H2 database
- **Controller Tests**: REST API testing with MockMvc

### **Test Data**
Use the provided test customer numbers:
- `234774784`
- `318411216`
- `340397370`
- `366585630`
- `397178638`

### **Running Tests**
```bash
# Run all tests
./gradlew test

# Run specific test class
./gradlew test --tests LoanServiceTest

# Run with coverage report
./gradlew test jacocoTestReport

# Integration tests only
./gradlew integrationTest
```

### **Test Coverage**
- **Total**: 49 comprehensive test methods
- **Service Layer**: 85%+ coverage
- **Controller Layer**: 90%+ coverage
- **Integration**: Full workflow testing

## 📊 **Monitoring & Observability**

### **Health Checks**
- **Application Health**: `/api/v1/health`
- **Actuator Health**: `/actuator/health`
- **Database Health**: `/actuator/health/db`

### **Metrics & Monitoring**
- **Metrics Endpoint**: `/actuator/metrics`
- **Loan Processing Metrics**: Success/failure rates
- **External Service Metrics**: Response times and error rates

### **Logging**
```json
{
    "timestamp": "2025-05-30T10:15:30.123Z",
    "level": "INFO",
    "logger": "com.lendingplatform.service.impl.LoanServiceImpl",
    "message": "Processing loan request for customer: 234774784, amount: 10000",
    "customerNumber": "234774784",
    "loanAmount": 10000
}
```

## 🚀 **Deployment**

### **Docker Deployment**
```dockerfile
FROM openjdk:17-jre-slim
COPY build/libs/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app.jar"]
```

```bash
# Build Docker image
docker build -t lms-app .

# Run container
docker run -p 8080:8080 -e SPRING_PROFILES_ACTIVE=prod lms-app
```

### **Production Deployment**
```bash
# Create production JAR
./gradlew bootJar

# Deploy with production profile
java -jar build/libs/lending-management-system-0.0.1-SNAPSHOT.jar \
  --spring.profiles.active=prod \
  --server.port=8080
```

## 🔧 **External Service Integration**

### **Core Banking System (CBS)**
- **KYC API**: Customer validation via SOAP
- **Transaction API**: Historical transaction data via SOAP
- **Authentication**: Basic auth (admin:pwd123)

### **Scoring Engine**
- **Registration**: Auto-registers on startup
- **Scoring Process**: Two-step async scoring
- **Authentication**: Token-based

### **Integration Flow**
```
LMS → CBS KYC (SOAP) → Customer Validation
LMS → Scoring Engine (REST) → Credit Assessment
Scoring Engine → LMS Transaction API (REST) → Transaction Data
LMS → CBS Transaction (SOAP) → Historical Data
```

## 📚 **API Documentation**

### **Swagger UI**
Access comprehensive API documentation at:
`http://localhost:8080/swagger-ui.html`

### **OpenAPI Specification**
Download API specification at:
`http://localhost:8080/api-docs`

## 🤝 **Development Guidelines**

### **Code Standards**
- Follow SOLID principles
- Clean code practices
- Comprehensive logging
- Unit test coverage > 80%

### **Architecture Patterns**
- Layered architecture (Controller → Service → Repository)
- Dependency injection
- Configuration externalization
- Error handling with global exception handler

## 🐛 **Troubleshooting**

### **Common Issues**

#### **External Service Connection Issues**
```bash
# Check CBS connectivity
curl -X POST "https://kycapidevtest.credable.io/service"
```

#### **Database Connection Issues**
```bash
# Check H2 console (development)
http://localhost:8080/h2-console

# Check PostgreSQL connection
psql -h localhost -p 5432 -U lms_user -d lms
```

### **Error Codes**
- **400**: Invalid request data or business rule violation
- **404**: Customer not found or no loan exists
- **409**: Customer already subscribed or has ongoing loan
- **500**: Internal server error or external service failure

## 📋 **Trade-offs & Assumptions**

### **Current Implementation**
- **Authentication**: Basic auth (production would use JWT/OAuth)
- **Database**: H2 for development (PostgreSQL for production)
- **External Services**: Real integration with test endpoints
- **Caching**: Simple in-memory caching
- **Error Handling**: Graceful degradation with mock data fallback

### **Production Considerations**
- Implement JWT/OAuth authentication
- Add rate limiting and throttling
- Implement circuit breaker pattern
- Add comprehensive monitoring (New Relic, Grafana)
- Database connection pooling and clustering
- API versioning strategy

## 🤝 **Contributing**

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Write comprehensive tests
4. Commit your changes (`git commit -m 'Add amazing feature'`)
5. Push to the branch (`git push origin feature/amazing-feature`)
6. Open a Pull Request

## 📄 **License**

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 📞 **Support**

For support and questions:
- **Email**: support@lendingplatform.com
- **Documentation**: [API Docs](http://localhost:8080/lender/api/v1/swagger-ui/index.html#/)
- **Issues**: [GitHub Issues](https://github.com/your-org/lms/issues)

---

**Built with ❤️ by the LMS Development Team**