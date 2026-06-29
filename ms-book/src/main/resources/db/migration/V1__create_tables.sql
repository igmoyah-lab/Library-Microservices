create table book (
    id uuid primary key,
    title varchar(255) not null,
    author varchar(255) not null,
    category varchar(255) not null,
    isbn varchar(255) not null unique
);
