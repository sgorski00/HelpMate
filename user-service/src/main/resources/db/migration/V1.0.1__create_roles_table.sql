CREATE TABLE IF NOT EXISTS roles (
    name VARCHAR(255) PRIMARY KEY
);

CREATE TABLE IF NOT EXISTS user_roles (
    user_id UUID,
    role_name VARCHAR(255),
    PRIMARY KEY(user_id, role_name),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (role_name) REFERENCES roles(name) ON DELETE CASCADE
);