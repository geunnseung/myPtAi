CREATE TABLE meal_record (
    id BIGINT NOT NULL AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    recorded_on DATE NOT NULL,
    meal_type VARCHAR(30) NOT NULL,
    name VARCHAR(100) NOT NULL,
    calories INT NULL,
    protein_g DECIMAL(6, 2) NULL,
    carbs_g DECIMAL(6, 2) NULL,
    fat_g DECIMAL(6, 2) NULL,
    memo VARCHAR(500) NULL,
    created_at DATETIME(6) NOT NULL,
    updated_at DATETIME(6) NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_meal_record_app_user
        FOREIGN KEY (user_id) REFERENCES app_user (id)
        ON DELETE RESTRICT
);

CREATE INDEX idx_meal_record_user_recorded_on
    ON meal_record (user_id, recorded_on);
