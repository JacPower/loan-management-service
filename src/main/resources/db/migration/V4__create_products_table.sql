CREATE TABLE IF NOT EXISTS products (
      id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
      product_name VARCHAR(50) NOT NULL,
      description VARCHAR(255),
      tenure_type VARCHAR(20) NOT NULL,
      tenure_value INTEGER NOT NULL,
      days_after_due_for_late_fee INTEGER NOT NULL,
      product_status VARCHAR(20) NOT NULL,
      is_fixed_term BOOLEAN NOT NULL DEFAULT true,
      is_notification_enabled BOOLEAN NOT NULL DEFAULT true,
      created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
      updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
      created_by VARCHAR(36),
      updated_by VARCHAR(36)
);