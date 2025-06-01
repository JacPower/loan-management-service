# Lending Management Service (LMS)

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
git clone git@github.com:JacPower/loan-management-service.git
cd lending-management-service
```

### **2. Build the Project**
```bash
./gradlew clean build
```

### **3. Run the Application**

#### **Development Mode**
```bash
./gradlew bootJar
java -jar build/libs/lending-management-system-0.0.1-SNAPSHOT.jar --spring.profiles.active=dev
```

### **4. Access the Application**
- **Application**: `http://localhost:8080`
- **API Documentation**: `http://localhost:8080/lender/api/v1/swagger-ui.html`

## ⚙️ **Configuration**

### **Application Properties**
The application uses `application.properties` for configuration management:

```properties
# Server Configuration
server.port=8080
spring.application.name=lender

# Database Configuration (Development)
 spring.datasource.url=jdbc:postgresql://localhost:5432/lender
 spring.datasource.username=your_user
 spring.datasource.password=your_password

# CBS SOAP Configuration
cbs.kyc.wsdl.url=https://kycapidevtest.credable.io/service
cbs.transaction.wsdl.url=https://trxapidevtest.credable.io/service
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
```

## 📡 **API Endpoints**

### **Loan Management APIs**

#### **Customer Subscription**
```http
POST /api/v1/loans/subscribe
Content-Type: application/json

{
    "customerNumber": "234774784"
}
```

#### **Loan Request**
```http
POST /api/v1/loans/request
Content-Type: application/json

{
    "customerNumber": "234774784",
    "amount": 15000
}
```

#### **Loan Status**
```http
GET /api/v1/loans/status/{customerNumber}
```

### **Transaction Data API (For Scoring Service)**

#### **Get Transaction Data**
```http
GET /api/v1/transaction-data/{customerNumber}
```

## 🔐 **Authentication & Security**

### **User Accounts**
- **Admin User**: `admin:admin123` (ADMIN role)
- **Scoring Service User**: `lms_user:lms_pass_123` (USER role)

### **Security Headers**

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
CREATE TABLE IF NOT EXISTS customers (
     id BIGSERIAL PRIMARY KEY,
     customer_number VARCHAR(50) UNIQUE NOT NULL,
     first_name VARCHAR(30),
     last_name VARCHAR(30),
     middle_name VARCHAR(30),
     phone_number VARCHAR(12),
     email VARCHAR(50),
     gender VARCHAR(50),
     id_number VARCHAR(50),
     id_number_type VARCHAR(50),
     monthly_income DOUBLE PRECISION DEFAULT 0,
     is_active BOOLEAN DEFAULT TRUE,
     scoring_token VARCHAR(50),
     registration_date TIMESTAMP WITHOUT TIME ZONE,
     created_at TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP,
     updated_at TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP
);
```

#### **Loan Table**
```sql
CREATE TABLE IF NOT EXISTS loans (
     id BIGSERIAL PRIMARY KEY,
     customer_number VARCHAR(255) NOT NULL,
     requested_amount DOUBLE PRECISION DEFAULT 0,
     approved_amount DOUBLE PRECISION DEFAULT 0,
     status VARCHAR(50) NOT NULL,
     credit_score INTEGER,
     credit_limit DOUBLE PRECISION DEFAULT 0,
     exclusion VARCHAR(255),
     exclusion_reason VARCHAR(500),
     application_date TIMESTAMP WITHOUT TIME ZONE NOT NULL,
     approval_date TIMESTAMP WITHOUT TIME ZONE,
     disbursement_date TIMESTAMP WITHOUT TIME ZONE,
     retry_count INTEGER DEFAULT 0,
     created_at TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP,
     updated_at TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP,

    -- Constraints
     CONSTRAINT loans_status_check CHECK (
         status IN ('PENDING', 'SCORING_IN_PROGRESS', 'APPROVED', 'REJECTED', 'DISBURSED', 'FAILED')
         ),
     CONSTRAINT loans_requested_amount_positive CHECK (requested_amount > 0),
     CONSTRAINT loans_approved_amount_positive CHECK (approved_amount IS NULL OR approved_amount > 0),
     CONSTRAINT loans_credit_score_range CHECK (credit_score IS NULL OR (credit_score >= 0 AND credit_score <= 1000)),
     CONSTRAINT loans_credit_limit_positive CHECK (credit_limit IS NULL OR credit_limit >= 0),
     CONSTRAINT loans_retry_count_non_negative CHECK (retry_count >= 0),

    -- Foreign key relationship (soft reference)
     CONSTRAINT fk_loans_customer_number
         FOREIGN KEY (customer_number)
             REFERENCES customers(customer_number)
             ON DELETE RESTRICT
             ON UPDATE CASCADE
);
```

## 🧪 **Testing**

### **Test Categories**
- **Unit Tests**: Service layer with mocked dependencies
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
- **Service Layer**: 85%+ coverage
- **Controller Layer**: 90%+ coverage

## 📊 **Monitoring & Observability**

### **Health Checks**

### **Metrics & Monitoring**


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
docker run -p 8080:8080 -e SPRING_PROFILES_ACTIVE=dev lms-app
```

### **Production Deployment**
```bash
# Create production JAR
./gradlew bootJar

# Deploy with production profile
java -jar build/libs/lending-management-system-0.0.1-SNAPSHOT.jar \
  --spring.profiles.active=dev \
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
`http://localhost:8080/lender/api/v1/swagger-ui.html`

### **OpenAPI Specification**
Download API specification at:
`http://localhost:8080/lender/api/v1/api-docs`

## 🤝 **Development Guidelines**

### **Code Standards**
- Follow SOLID principles
- Clean code practices
- Comprehensive logging
- Unit test coverage > 85%

### **Architecture Patterns**
- Layered architecture (Controller → Service → Repository)
- Dependency injection
- Configuration externalization
- Error handling with global exception handler

## 📋 **Trade-offs & Assumptions**

### **Current Implementation**
- **Authentication**: Basic auth (production would use JWT/OAuth)
- **Database**: PostgreSQL for development
- **External Services**: Real integration with test endpoints
- **Error Handling**: Graceful degradation with mock data fallback

### **Production Considerations**
- Implement JWT/OAuth authentication
- Add rate limiting and throttling
- Add redis cache
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
- **Issues**: [GitHub Issues](https://github.com)

---

**Built with ❤️ by the LMS Development Team**