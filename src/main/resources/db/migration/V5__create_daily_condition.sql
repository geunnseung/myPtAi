CREATE TABLE daily_condition (
    id BIGINT NOT NULL AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    recorded_on DATE NOT NULL,
    sleep_minutes INT NULL,
    body_weight_kg DECIMAL(5, 2) NULL,
    energy_level VARCHAR(30) NULL,
    memo VARCHAR(1000) NULL,
    created_at DATETIME(6) NOT NULL,
    updated_at DATETIME(6) NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_daily_condition_app_user
        FOREIGN KEY (user_id) REFERENCES app_user (id)
        ON DELETE RESTRICT,
    CONSTRAINT uk_daily_condition_user_recorded_on
        UNIQUE (user_id, recorded_on)
);
