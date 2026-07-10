CREATE TABLE fines (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL,
    loan_id UUID NOT NULL UNIQUE,
    amount NUMERIC(10, 2) NOT NULL,
    fine_date DATE NOT NULL,
    status VARCHAR(20) NOT NULL
);