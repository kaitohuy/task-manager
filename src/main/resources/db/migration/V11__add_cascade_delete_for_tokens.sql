-- Update verification_tokens constraint to ON DELETE CASCADE
ALTER TABLE verification_tokens DROP CONSTRAINT IF EXISTS fk_verification_token_user;
ALTER TABLE verification_tokens ADD CONSTRAINT fk_verification_token_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE;

-- Update password_reset_tokens constraint to ON DELETE CASCADE
ALTER TABLE password_reset_tokens DROP CONSTRAINT IF EXISTS fk_password_reset_token_user;
ALTER TABLE password_reset_tokens ADD CONSTRAINT fk_password_reset_token_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE;
