CREATE TABLE IF NOT EXISTS fees (
      id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
      fee_name VARCHAR(30) NOT NULL,
      fee_type VARCHAR(20) NOT NULL,
      calculation_type VARCHAR(20) NOT NULL,
      fee_value DECIMAL(12, 2) NOT NULL,
      application_timing VARCHAR(20) NOT NULL,
      description VARCHAR(255) NOT NULL,
      is_active BOOLEAN NOT NULL DEFAULT true,
      created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
      updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
      created_by VARCHAR(36),
      updated_by VARCHAR(36)
);