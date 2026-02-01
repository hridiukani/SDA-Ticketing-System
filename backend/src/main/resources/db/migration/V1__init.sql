CREATE TABLE users (
    id SERIAL PRIMARY KEY,
    email VARCHAR(100) NOT NULL UNIQUE,
    password_hash VARCHAR(200) NOT NULL,
    full_name VARCHAR(100) NOT NULL,
    role VARCHAR(20) NOT NULL,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT NOW()
);

CREATE TABLE categories (
    id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    description VARCHAR(500),
    active BOOLEAN NOT NULL DEFAULT TRUE
);

CREATE TABLE sla_policies (
    id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    description VARCHAR(500),
    target_resolution_hours INT NOT NULL,
    applicable_priority VARCHAR(20) NOT NULL,
    active BOOLEAN NOT NULL DEFAULT TRUE
);

CREATE TABLE tickets (
    id SERIAL PRIMARY KEY,
    title VARCHAR(200) NOT NULL,
    description VARCHAR(4000) NOT NULL,
    status VARCHAR(30) NOT NULL,
    priority VARCHAR(20) NOT NULL,
    category_id INT REFERENCES categories (id),
    requester_id INT NOT NULL REFERENCES users (id),
    assignee_id INT REFERENCES users (id),
    sla_policy_id INT REFERENCES sla_policies (id),
    created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT NOW(),
    resolved_at TIMESTAMP WITHOUT TIME ZONE,
    due_at TIMESTAMP WITHOUT TIME ZONE
);

CREATE TABLE comments (
    id SERIAL PRIMARY KEY,
    ticket_id INT NOT NULL REFERENCES tickets (id),
    author_id INT NOT NULL REFERENCES users (id),
    content VARCHAR(4000) NOT NULL,
    internal_comment BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT NOW()
);

CREATE TABLE attachments (
    id SERIAL PRIMARY KEY,
    ticket_id INT NOT NULL REFERENCES tickets (id),
    uploaded_by_id INT NOT NULL REFERENCES users (id),
    file_name VARCHAR(255) NOT NULL,
    content_type VARCHAR(255) NOT NULL,
    size_bytes BIGINT NOT NULL,
    storage_path VARCHAR(500) NOT NULL,
    uploaded_at TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT NOW()
);

CREATE TABLE audit_logs (
    id SERIAL PRIMARY KEY,
    ticket_id INT NOT NULL REFERENCES tickets (id),
    actor_id INT REFERENCES users (id),
    action_type VARCHAR(100) NOT NULL,
    details VARCHAR(1000),
    created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT NOW()
);

INSERT INTO users (email, password_hash, full_name, role)
VALUES
  ('admin@example.com', '$2a$10$adminhashadminhashadminha', 'Admin User', 'ADMIN'),
  ('tech@example.com', '$2a$10$techhashtechhashtechhash', 'Tech User', 'TECHNICIAN'),
  ('requester@example.com', '$2a$10$requesterhashrequester', 'Requester User', 'REQUESTER');

INSERT INTO categories (name, description) VALUES
  ('Hardware', 'Hardware-related issues'),
  ('Software', 'Software-related issues'),
  ('Network', 'Network connectivity issues');

