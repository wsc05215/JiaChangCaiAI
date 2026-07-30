-- =============================================
-- 收藏模块：建表 + 测试数据
-- =============================================

CREATE TABLE IF NOT EXISTS favorite (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    recipe_id BIGINT NOT NULL COMMENT '菜谱ID',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '收藏时间',
    UNIQUE KEY uk_user_recipe (user_id, recipe_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='菜谱收藏表';

-- 测试数据：用户1收藏了5个菜谱，用户2收藏了4个，等
INSERT INTO favorite (user_id, recipe_id, created_at) VALUES
(1, 1, '2026-07-25 10:00:00'),
(1, 3, '2026-07-25 11:00:00'),
(1, 5, '2026-07-26 09:00:00'),
(1, 7, '2026-07-27 14:00:00'),
(1, 10, '2026-07-28 08:00:00'),
(2, 1, '2026-07-26 12:00:00'),
(2, 2, '2026-07-26 13:00:00'),
(2, 4, '2026-07-27 10:00:00'),
(2, 6, '2026-07-28 15:00:00'),
(3, 1, '2026-07-27 08:00:00'),
(3, 2, '2026-07-27 09:00:00'),
(3, 3, '2026-07-28 11:00:00'),
(4, 8, '2026-07-28 16:00:00'),
(4, 9, '2026-07-28 17:00:00'),
(4, 11, '2026-07-29 07:00:00'),
(5, 12, '2026-07-29 10:00:00'),
(5, 13, '2026-07-29 11:00:00');
