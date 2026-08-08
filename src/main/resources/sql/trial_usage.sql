CREATE TABLE IF NOT EXISTS trial_usage (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL COMMENT '用户ID',
    ai_type VARCHAR(50) NOT NULL COMMENT 'CustomizedRecipe / Oneclickmenu / AiFridgeFoodService',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX idx_user_type (user_id, ai_type)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='非会员试用记录表';
