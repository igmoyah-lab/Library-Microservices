CREATE EXTENSION IF NOT EXISTS "pgcrypto";

DROP INDEX IF EXISTS idx_users_username;

DROP TABLE IF EXISTS users;

CREATE TABLE user_profiles (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    auth_email VARCHAR(255) UNIQUE NOT NULL,
    full_name VARCHAR(255),
    phone VARCHAR(50),
    address VARCHAR(255)
);

CREATE INDEX idx_user_profiles_auth_email ON user_profiles(auth_email);