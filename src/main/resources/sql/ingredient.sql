-- ingredient 建表（新部署用）
-- 注意：quantity/unit 两个字段为拍照识别功能新增
CREATE TABLE IF NOT EXISTS ingredient (
    ingredient_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL COMMENT '所属用户ID',
    name VARCHAR(100) NOT NULL COMMENT '食材名称',
    category VARCHAR(20) DEFAULT '其他' COMMENT '分类：蔬菜/生禽/蛋类/水产/豆制品/其他',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '存入时间',
    storage_method VARCHAR(20) DEFAULT '冷藏' COMMENT '储存方式：冷藏/冷冻/常温',
    purchase_date DATE DEFAULT NULL COMMENT '购买日期',
    expire_days INT DEFAULT NULL COMMENT '保质天数',
    expire_date DATETIME DEFAULT NULL COMMENT '过期日期（purchase_date + expire_days）',
    quantity INT DEFAULT 1 COMMENT '数量（拍照识别时模型返回）',
    unit VARCHAR(20) DEFAULT NULL COMMENT '单位（个/斤/盒 等）',
    INDEX idx_user (user_id),
    INDEX idx_user_time (user_id, create_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户食材表';

-- 已有数据库增量升级（一次执行即可；重复执行会报"字段已存在"，忽略即可）
ALTER TABLE ingredient ADD COLUMN quantity INT DEFAULT 1 COMMENT '数量（拍照识别时模型返回）' AFTER expire_date;
ALTER TABLE ingredient ADD COLUMN unit VARCHAR(20) DEFAULT NULL COMMENT '单位（个/斤/盒 等）' AFTER quantity;
