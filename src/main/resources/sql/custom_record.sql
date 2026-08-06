CREATE TABLE IF NOT EXISTS custom_record (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    type VARCHAR(50) NOT NULL COMMENT 'CustomizedRecipe / Oneclickmenu',
    title VARCHAR(255) DEFAULT '' COMMENT '自动提取的标题',
    content LONGTEXT COMMENT 'AI生成的完整内容',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX idx_user_type (user_id, type),
    INDEX idx_user_time (user_id, create_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='AI定制记录表';
