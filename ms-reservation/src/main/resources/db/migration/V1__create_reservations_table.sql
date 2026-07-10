CREATE TABLE reservations (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL,
    book_id UUID NOT NULL,
    reservation_date DATE NOT NULL,
    status VARCHAR(20) NOT NULL
);