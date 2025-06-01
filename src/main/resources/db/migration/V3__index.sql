-- Create indexes for customers table
CREATE INDEX IF NOT EXISTS idx_customers_customer_number ON customers(customer_number);

-- Create indexes for loans table
CREATE INDEX IF NOT EXISTS idx_loans_customer_number ON loans(customer_number);