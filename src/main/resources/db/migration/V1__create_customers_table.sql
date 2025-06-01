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