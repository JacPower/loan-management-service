Lending Application Quick Start

This is a Spring Boot application designed to manage lending operations, including product creation, loan disbursement, 
repayment handling and notifications.

# ==============
Key Features

    Product management with configurable tenure and fees
    Loan lifecycle management
    Customer profile management
    Automated notification system
    Payment and installment tracking
    Fee calculation and application

# ==============
Technology Stack

    Language: Java 17
    Framework: Spring Boot
    Database: PostgreSQL
    Build Tool: Gradle
    Tools:
    
    Spring Data JPA
    Lombok
    Flyway (Migrations)

# ==============
System Requirements
Prerequisites

    Java 17+
    Gradle 7.x+
    PostgreSQL 12+
    Git

# ==============
Installation

    1. Clone the repository: git clone <repository-url>
    2. Build the project with Gradle: ./gradlew clean build
    3. Run the application: ./gradlew bootRun --args='--spring.profiles.active=local'
    4. Postman documentation: https://documenter.getpostman.com/view/37602788/2sB2j4eqJZ

# ==============
Configuration
Environment Variables

The application requires several environment variables for configuration, including:

    spring.datasource.url=jdbc:postgresql://localhost:5432/lender
    spring.datasource.username=your_username
    spring.datasource.password=your_password
    spring.jpa.hibernate.ddl-auto=none
    spring.flyway.baseline-on-migrate=true

Alternatively, you can modify application-local.properties for local development.

    # Server Configuration
    server.port=8080
    server.servlet.context-path=/lender/v1
    
    # Spring Configuration
    spring.application.name=lender
    
    # Database Configuration
    spring.datasource.url=jdbc:postgresql://localhost:5432/lender
    spring.datasource.username=postgres
    spring.datasource.password=postgres
    
    # JPA/Hibernate Configuration
    spring.jpa.show-sql=false
    spring.jpa.hibernate.ddl-auto=none
    spring.jpa.properties.hibernate.format_sql=true
    spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect

# ==============
Database Migrations
Flyway Setup in application-local.properties

    spring.flyway.baseline-on-migrate=true
    spring.flyway.locations=classpath:db/migration
    spring.flyway.enabled=true
    spring.flyway.schemas=public
    spring.flyway.url=jdbc:postgresql://localhost:5432/lender
    spring.flyway.user=postgres
    spring.flyway.password=postgres

You can run database migrations with Flyway by including the following task:
./gradlew flywayMigrate

# ==============
Notification Configuration

To configure notification settings, ensure the following properties are set:
Notification Setup in application-local.properties

    # Default notification channel
    notification.default.channel=SMS
    
    # Global notification enabled flags
    notification.enabled.payment_reminder=true
    notification.enabled.installment_reminder=true
    notification.enabled.loan_overdue=true
    notification.enabled.loan_default=true
    notification.enabled.loan_written_off=true
    notification.enabled.loan_closed=true
    notification.enabled.loan_creation=true
    notification.enabled.payment_received=true

Notification Templates

    notification.template.payment_reminder=Dear {{firstName}}, This is a reminder that your loan payment of {{currentBalance}} is due on {{dueDate}}.
    notification.template.installment_reminder=Dear {{firstName}}, This is a reminder that your loan installment of {{installmentAmount}} is due on {{installmentDueDate}}.
    notification.template.loan_overdue=Dear {{firstName}}, Your loan payment of {{currentBalance}} was due on {{dueDate}} and is now {{daysOverdue}} days overdue.

# ==============
Testing

To run tests, execute the following command:
./gradlew test

# ==============
API Endpoints

# Product Management

    GET    /lender/v1/products              - List all products
    POST   /lender/v1/products              - Create a new product
    GET    /lender/v1/products/{id}         - Get product details
    PUT    /lender/v1/products/{id}         - Update product

# Customer Management

    GET    /lender/v1/customers             - List all customers
    POST   /lender/v1/customers             - Create a new customer
    GET    /lender/v1/customers/{id}        - Get customer details
    PUT    /lender/v1/customers/{id}        - Update customer

# Loan Management

    GET    /lender/v1/loans                 - List all loans
    POST   /lender/v1/loans                 - Create a new loan
    POST   /lender/v1/loans/{id}/pay        - Make a loan payment
    GET    /lender/v1/loans/{id}            - Get loan details
    GET   /lender/v1/loans/{id}/payments   - Get loan payment

# Fee Management

    GET    /lender/v1/fees                  - List all fees
    POST   /lender/v1/fees                  - Create a new fee
    GET    /lender/v1/fees/{id}             - Get fee details
    PUT    /lender/v1/fees/{id}             - Update fee

# ==============
Trade-offs & Assumptions

This implementation includes several deliberate trade-offs made for the scope of this project:

    Authentication assumed at API gateway level
    No actual notification dispatch (SMS/Email sending)
    No actual payment gateway integration
    No admin UI/portal for management

# ==============
Production Deployment

To deploy the application in production:
./gradlew bootJar
java -jar build/libs/lender-1.0.0.jar --spring.profiles.active=prod

# ==============
Postman Documentation

    https://documenter.getpostman.com/view/37602788/2sB2j4eqJZ

# ==============
Additional Notes

    Database migrations will run automatically on application startup.
    All notifications are stored in the database but not actually sent.
    Payment transactions are recorded but no actual money transfer occurs.
    Spring security is not configured.
    Unit tests not completed.

# ==============
Database ER Diagram

![db_er_diagram](https://github.com/user-attachments/assets/03f59d28-571d-44ce-9ce1-0b64ecaf5727)


