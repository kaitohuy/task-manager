-- Add enabled and verified columns to users table
ALTER TABLE users ADD COLUMN enabled BOOLEAN DEFAULT TRUE;
ALTER TABLE users ADD COLUMN verified BOOLEAN DEFAULT FALSE;

-- Update existing users to be enabled and verified (assume existing users are valid)
UPDATE users SET enabled = TRUE, verified = TRUE;

-- Add columns to audit table if it exists
DO $$ 
BEGIN 
    IF EXISTS (SELECT FROM information_schema.tables WHERE table_name = 'users_aud') THEN
        ALTER TABLE users_aud ADD COLUMN enabled BOOLEAN DEFAULT TRUE;
        ALTER TABLE users_aud ADD COLUMN verified BOOLEAN DEFAULT FALSE;
    END IF;
END $$;

-- Create verification_tokens table
CREATE TABLE verification_tokens (
    id BIGSERIAL PRIMARY KEY,
    token VARCHAR(255) NOT NULL UNIQUE,
    user_id BIGINT NOT NULL,
    expiry_date TIMESTAMP NOT NULL,
    CONSTRAINT fk_verification_token_user FOREIGN KEY (user_id) REFERENCES users(id)
);

-- Create password_reset_tokens table
CREATE TABLE password_reset_tokens (
    id BIGSERIAL PRIMARY KEY,
    token VARCHAR(255) NOT NULL UNIQUE,
    user_id BIGINT NOT NULL,
    expiry_date TIMESTAMP NOT NULL,
    CONSTRAINT fk_password_reset_token_user FOREIGN KEY (user_id) REFERENCES users(id)
);
