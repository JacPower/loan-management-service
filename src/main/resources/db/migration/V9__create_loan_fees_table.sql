CREATE TABLE IF NOT EXISTS loan_fees (
       id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
       loan_id UUID NOT NULL REFERENCES loans(id),
       fee_id UUID NOT NULL REFERENCES fees(id),
       amount DECIMAL(12, 2) NOT NULL,
       applied_at TIMESTAMP NOT NULL,
       created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
       updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
       created_by VARCHAR(36),
       updated_by VARCHAR(36)
);