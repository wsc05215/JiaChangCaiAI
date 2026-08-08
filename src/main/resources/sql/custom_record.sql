-- custom_record 建表（新部署用）
CREATE TABLE IF NOT EXISTS custom_record (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    type VARCHAR(50) NOT NULL COMMENT 'CustomizedRecipe / Oneclickmenu',
    title VARCHAR(255) DEFAULT '' COMMENT '自动提取的标题',
    description VARCHAR(500) DEFAULT '' COMMENT '菜品简介',
    cook_time VARCHAR(50) DEFAULT '' COMMENT '烹饪时长',
    difficulty VARCHAR(20) DEFAULT '' COMMENT '难度等级',
    ingredients TEXT COMMENT '食材清单 JSON',
    steps TEXT COMMENT '烹饪步骤 JSON',
    content LONGTEXT COMMENT 'AI生成的完整原始内容',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX idx_user_type (user_id, type),
    INDEX idx_user_time (user_id, create_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='AI定制记录表';

-- 已有数据库增量升级（如果字段已存在会报错，忽略即可）
-- ALTER TABLE custom_record ADD COLUMN description VARCHAR(500) DEFAULT '' COMMENT '菜品简介' AFTER title;
-- ALTER TABLE custom_record ADD COLUMN cook_time VARCHAR(50) DEFAULT '' COMMENT '烹饪时长' AFTER description;
-- ALTER TABLE custom_record ADD COLUMN difficulty VARCHAR(20) DEFAULT '' COMMENT '难度等级' AFTER cook_time;
-- ALTER TABLE custom_record ADD COLUMN ingredients TEXT COMMENT '食材清单 JSON' AFTER difficulty;
-- ALTER TABLE custom_record ADD COLUMN steps TEXT COMMENT '烹饪步骤 JSON' AFTER ingredients;
