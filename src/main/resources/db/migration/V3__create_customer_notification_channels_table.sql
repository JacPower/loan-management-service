CREATE TABLE IF NOT EXISTS customer_notification_channels (
    id UUID DEFAULT uuid_generate_v4() NOT NULL,
    customer_id UUID NOT NULL,
    channel VARCHAR(20) NOT NULL,
    is_active BOOLEAN NOT NULL DEFAULT true,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(36),
    updated_by VARCHAR(36),
    PRIMARY KEY (id),
    FOREIGN KEY (customer_id) REFERENCES customers(id),
    UNIQUE (customer_id, channel)
);