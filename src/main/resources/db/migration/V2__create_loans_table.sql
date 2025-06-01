CREATE TABLE IF NOT EXISTS loans (
     id BIGSERIAL PRIMARY KEY,
     customer_number VARCHAR(255) NOT NULL,
     requested_amount DOUBLE PRECISION DEFAULT 0,
     approved_amount DOUBLE PRECISION DEFAULT 0,
     status VARCHAR(50) NOT NULL,
     credit_score INTEGER,
     credit_limit DOUBLE PRECISION DEFAULT 0,
     exclusion VARCHAR(255),
     exclusion_reason VARCHAR(500),
     application_date TIMESTAMP WITHOUT TIME ZONE NOT NULL,
     approval_date TIMESTAMP WITHOUT TIME ZONE,
     disbursement_date TIMESTAMP WITHOUT TIME ZONE,
     retry_count INTEGER DEFAULT 0,
     created_at TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP,
     updated_at TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP,

    -- Constraints
     CONSTRAINT loans_status_check CHECK (
         status IN ('PENDING', 'SCORING_IN_PROGRESS', 'APPROVED', 'REJECTED', 'DISBURSED', 'FAILED')
         ),
     CONSTRAINT loans_requested_amount_positive CHECK (requested_amount > 0),
     CONSTRAINT loans_approved_amount_positive CHECK (approved_amount IS NULL OR approved_amount > 0),
     CONSTRAINT loans_credit_score_range CHECK (credit_score IS NULL OR (credit_score >= 0 AND credit_score <= 1000)),
     CONSTRAINT loans_credit_limit_positive CHECK (credit_limit IS NULL OR credit_limit >= 0),
     CONSTRAINT loans_retry_count_non_negative CHECK (retry_count >= 0),

    -- Foreign key relationship (soft reference)
     CONSTRAINT fk_loans_customer_number
         FOREIGN KEY (customer_number)
             REFERENCES customers(customer_number)
             ON DELETE RESTRICT
             ON UPDATE CASCADE
);