-- Loans indexes
CREATE INDEX IF NOT EXISTS idx_loans_customer_id ON loans(customer_id);
CREATE INDEX IF NOT EXISTS idx_loans_product_id ON loans(product_id);
CREATE INDEX IF NOT EXISTS idx_loans_status ON loans(loan_status);
CREATE INDEX IF NOT EXISTS idx_loans_due_date ON loans(due_date);

CREATE INDEX IF NOT EXISTS idx_payments_loan_id ON payments(loan_id);

CREATE INDEX IF NOT EXISTS idx_notifications_customer_id ON notifications(customer_id);
CREATE INDEX IF NOT EXISTS idx_notifications_loan_id ON notifications(loan_id);

CREATE INDEX IF NOT EXISTS idx_installments_loan_id ON installments(loan_id);

CREATE INDEX IF NOT EXISTS idx_loan_fees_loan_id ON loan_fees(loan_id);
