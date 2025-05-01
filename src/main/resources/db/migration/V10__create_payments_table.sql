CREATE TABLE IF NOT EXISTS payments (
      id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
      loan_id UUID NOT NULL REFERENCES loans(id),
      amount DECIMAL(12, 2) NOT NULL,
      payment_date TIMESTAMP NOT NULL,
      payment_code VARCHAR(20) NOT NULL,
      payment_method VARCHAR(20) NOT NULL,
      payment_status VARCHAR(20) NOT NULL,
      created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
      updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
      created_by VARCHAR(36),
      updated_by VARCHAR(36)
);