-- Create permissions table
CREATE TABLE permissions (
    id SERIAL PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE,
    description VARCHAR(255)
);

-- Create role_permissions table
CREATE TABLE role_permissions (
    role_name VARCHAR(50) NOT NULL,
    permission_id INTEGER NOT NULL REFERENCES permissions(id),
    PRIMARY KEY (role_name, permission_id)
);

-- Insert Permissions
INSERT INTO permissions (name, description) VALUES
('USER_VIEW', 'view list user'),
('USER_EDIT', 'edit user information'),
('USER_DELETE', 'delete user'),
('PROJECT_VIEW', 'view project'),
('PROJECT_CREATE', 'create new project'),
('PROJECT_EDIT', 'edit project'),
('PROJECT_DELETE', 'delete project'),
('TASK_VIEW', 'view task'),
('TASK_CREATE', 'create new task'),
('TASK_EDIT', 'edit task'),
('TASK_DELETE', 'delete task'),
('COMMENT_CREATE', 'create new comment'),
('COMMENT_DELETE', 'delete comment'),
('ATTACHMENT_UPLOAD', 'create attachment'),
('MEETING_VIEW', 'view meeting'),
('MEETING_CREATE', 'create meeting');

-- Map Permissions to Roles
-- ADMIN: All permissions
INSERT INTO role_permissions (role_name, permission_id)
SELECT 'ADMIN', id FROM permissions;

-- MANAGER: Project and Task management
INSERT INTO role_permissions (role_name, permission_id)
SELECT 'MANAGER', id FROM permissions 
WHERE name IN ('PROJECT_VIEW', 'PROJECT_EDIT', 'TASK_VIEW', 'TASK_CREATE', 'TASK_EDIT', 'TASK_DELETE', 'COMMENT_CREATE', 'COMMENT_DELETE', 'ATTACHMENT_UPLOAD', 'MEETING_VIEW', 'MEETING_CREATE');

-- MEMBER: View and Interaction
INSERT INTO role_permissions (role_name, permission_id)
SELECT 'MEMBER', id FROM permissions 
WHERE name IN ('PROJECT_VIEW', 'TASK_VIEW', 'TASK_EDIT', 'COMMENT_CREATE', 'ATTACHMENT_UPLOAD', 'MEETING_VIEW');
