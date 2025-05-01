CREATE TABLE IF NOT EXISTS installments (
      id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
      loan_id UUID NOT NULL REFERENCES loans(id),
      amount DECIMAL(12, 2) NOT NULL,
      amount_paid DECIMAL(12, 2) NOT NULL,
      due_date DATE NOT NULL,
      installment_status VARCHAR(20) NOT NULL,
      created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
      updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
      created_by VARCHAR(36),
      updated_by VARCHAR(36)
);