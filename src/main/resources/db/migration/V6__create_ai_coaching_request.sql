CREATE TABLE ai_coaching_request (
    id BIGINT NOT NULL AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    target_date DATE NOT NULL,
    question VARCHAR(1000) NOT NULL,
    answer VARCHAR(10000) NULL,
    model VARCHAR(100) NULL,
    status VARCHAR(30) NOT NULL,
    error_message VARCHAR(1000) NULL,
    created_at DATETIME(6) NOT NULL,
    updated_at DATETIME(6) NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_ai_coaching_request_app_user
        FOREIGN KEY (user_id) REFERENCES app_user (id)
        ON DELETE RESTRICT
);

CREATE INDEX idx_ai_coaching_request_user_target_date_created_at
    ON ai_coaching_request (user_id, target_date, created_at);
