ALTER TABLE app_user
    ADD COLUMN email VARCHAR(255) NULL;

ALTER TABLE app_user
    ADD COLUMN password_hash VARCHAR(100) NULL;

ALTER TABLE app_user
    ADD COLUMN role VARCHAR(30) NOT NULL DEFAULT 'USER';

CREATE UNIQUE INDEX uk_app_user_email
    ON app_user (email);
