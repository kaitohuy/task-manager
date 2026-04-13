-- Add MFA fields to users table
ALTER TABLE users ADD COLUMN mfa_enabled BOOLEAN DEFAULT FALSE;
ALTER TABLE users ADD COLUMN mfa_secret VARCHAR(255);

-- Add to audit table
DO $$ 
BEGIN 
    IF EXISTS (SELECT FROM information_schema.tables WHERE table_name = 'users_aud') THEN
        ALTER TABLE users_aud ADD COLUMN mfa_enabled BOOLEAN DEFAULT FALSE;
    END IF;
END $$;
