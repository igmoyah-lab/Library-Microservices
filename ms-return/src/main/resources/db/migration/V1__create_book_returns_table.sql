CREATE TABLE book_returns (
    id UUID PRIMARY KEY,
    loan_id UUID NOT NULL UNIQUE,
    return_date DATE NOT NULL,
    delayed BOOLEAN NOT NULL
);