-- Create user_permissions table for individual assignment with Allow/Deny support
CREATE TABLE user_permissions (
    id SERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    permission_id INTEGER NOT NULL REFERENCES permissions(id) ON DELETE CASCADE,
    is_denied BOOLEAN NOT NULL DEFAULT FALSE,
    UNIQUE (user_id, permission_id)
);
