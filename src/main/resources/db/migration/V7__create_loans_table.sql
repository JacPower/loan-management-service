CREATE TABLE IF NOT EXISTS loans (
       id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
       product_id UUID NOT NULL REFERENCES products(id),
       customer_id UUID NOT NULL REFERENCES customers(id),
       principal_amount DECIMAL(12, 2) NOT NULL,
       disbursement_date DATE NOT NULL,
       due_date DATE NOT NULL,
       loan_status VARCHAR(20) NOT NULL,
       loan_code VARCHAR(20) NOT NULL,
       structure_type VARCHAR(20) NOT NULL,
       current_balance DECIMAL(12, 2) NOT NULL,
       description VARCHAR(255),
       created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
       updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
       created_by VARCHAR(36),
       updated_by VARCHAR(36)
);