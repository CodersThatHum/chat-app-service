DROP TABLE IF EXISTS "users";
CREATE TABLE IF NOT EXISTS "users"
(
    id            BIGSERIAL PRIMARY KEY,
    name          VARCHAR(255) NOT NULL,
    email         VARCHAR(255) NOT NULL UNIQUE,
    password      VARCHAR(255) NOT NULL,
    refresh_token VARCHAR(255),

--  AUDIT COLUMNS
    is_active     BOOLEAN      NOT NULL DEFAULT TRUE,
    created_at    TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by    VARCHAR(255),
    updated_at    TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_by    VARCHAR(255),
    deleted_at    TIMESTAMP,
    deleted_by    VARCHAR(255)
);