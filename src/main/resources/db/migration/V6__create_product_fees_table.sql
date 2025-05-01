CREATE TABLE IF NOT EXISTS product_fees (
      id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
      product_id UUID NOT NULL REFERENCES products(id),
      fee_id UUID NOT NULL REFERENCES fees(id),
      created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
      updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
      created_by VARCHAR(36),
      updated_by VARCHAR(36),
      UNIQUE (product_id, fee_id)
);