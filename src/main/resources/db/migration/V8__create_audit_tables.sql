CREATE TABLE users_aud (
    id BIGINT NOT NULL,
    rev BIGINT NOT NULL,
    revtype SMALLINT NOT NULL,
    username VARCHAR(255),
    PRIMARY KEY (id, rev),
    CONSTRAINT fk_users_aud_rev FOREIGN KEY (rev) REFERENCES revinfo(id)
);

CREATE INDEX idx_users_aud_rev ON users_aud(rev);

CREATE TABLE task_aud (
    id BIGINT NOT NULL,
    rev BIGINT NOT NULL,
    revtype SMALLINT NOT NULL,
    title VARCHAR(255),
    description TEXT,
    status VARCHAR(50),
    deadline TIMESTAMP,
    project_id BIGINT,
    assignee_id BIGINT,
    PRIMARY KEY (id, rev),
    CONSTRAINT fk_task_aud_rev FOREIGN KEY (rev) REFERENCES revinfo(id),
    CONSTRAINT fk_task_aud_project FOREIGN KEY (project_id) REFERENCES project(id),
    CONSTRAINT fk_task_aud_assignee FOREIGN KEY (assignee_id) REFERENCES users(id)
);

CREATE INDEX idx_task_aud_rev ON task_aud(rev);
CREATE INDEX idx_task_aud_project ON task_aud(project_id);
CREATE INDEX idx_task_aud_assignee ON task_aud(assignee_id);

CREATE TABLE project_aud (
    id BIGINT NOT NULL,
    rev BIGINT NOT NULL,
    revtype SMALLINT NOT NULL,
    name VARCHAR(255),
    description TEXT,
    created_by BIGINT,
    PRIMARY KEY (id, rev),
    CONSTRAINT fk_project_aud_rev FOREIGN KEY (rev) REFERENCES revinfo(id),
    CONSTRAINT fk_project_aud_created_by FOREIGN KEY (created_by) REFERENCES users(id)
);

CREATE INDEX idx_project_aud_rev ON project_aud(rev);

CREATE TABLE project_member_aud (
    id BIGINT NOT NULL,
    rev BIGINT NOT NULL,
    revtype SMALLINT NOT NULL,
    user_id BIGINT,
    project_id BIGINT,
    role VARCHAR(50),
    PRIMARY KEY (id, rev),
    CONSTRAINT fk_project_member_aud_rev FOREIGN KEY (rev) REFERENCES revinfo(id),
    CONSTRAINT fk_project_member_aud_user FOREIGN KEY (user_id) REFERENCES users(id),
    CONSTRAINT fk_project_member_aud_project FOREIGN KEY (project_id) REFERENCES project(id)
);

CREATE INDEX idx_project_member_aud_rev ON project_member_aud(rev);
CREATE INDEX idx_project_member_aud_user ON project_member_aud(user_id);
CREATE INDEX idx_project_member_aud_project ON project_member_aud(project_id);