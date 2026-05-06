CREATE TABLE app_user (
    id BIGINT NOT NULL AUTO_INCREMENT,
    display_name VARCHAR(50) NOT NULL,
    goal VARCHAR(50) NOT NULL,
    created_at DATETIME(6) NOT NULL,
    updated_at DATETIME(6) NOT NULL,
    PRIMARY KEY (id)
);
