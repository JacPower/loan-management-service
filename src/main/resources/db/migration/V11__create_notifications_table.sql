CREATE TABLE IF NOT EXISTS notifications (
       id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
       customer_id UUID NOT NULL REFERENCES customers(id),
       loan_id UUID REFERENCES loans(id),
       notification_type VARCHAR(50) NOT NULL,
       content TEXT NOT NULL,
       channel VARCHAR(20) NOT NULL,
       status VARCHAR(20) NOT NULL,
       sent_at TIMESTAMP,
       created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
       updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
       created_by VARCHAR(36),
       updated_by VARCHAR(36)
);