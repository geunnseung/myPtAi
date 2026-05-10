CREATE TABLE workout_record (
    id BIGINT NOT NULL AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    recorded_on DATE NOT NULL,
    workout_type VARCHAR(30) NOT NULL,
    title VARCHAR(100) NOT NULL,
    duration_minutes INT NULL,
    intensity VARCHAR(30) NULL,
    memo VARCHAR(1000) NULL,
    created_at DATETIME(6) NOT NULL,
    updated_at DATETIME(6) NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_workout_record_app_user
        FOREIGN KEY (user_id) REFERENCES app_user (id)
        ON DELETE RESTRICT
);

CREATE INDEX idx_workout_record_user_recorded_on
    ON workout_record (user_id, recorded_on);
