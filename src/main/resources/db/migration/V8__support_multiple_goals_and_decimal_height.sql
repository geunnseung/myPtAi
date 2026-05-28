CREATE TABLE app_user_goal (
    app_user_id BIGINT NOT NULL,
    sort_order INT NOT NULL,
    goal VARCHAR(50) NOT NULL,
    PRIMARY KEY (app_user_id, sort_order),
    CONSTRAINT fk_app_user_goal_app_user FOREIGN KEY (app_user_id) REFERENCES app_user (id)
);

INSERT INTO app_user_goal (app_user_id, sort_order, goal)
SELECT id, 0, goal
FROM app_user;

ALTER TABLE app_user
    DROP COLUMN goal;

ALTER TABLE app_user
    MODIFY COLUMN height_cm DECIMAL(4, 1) NULL;
