-- =============================================
-- 商品模块：建表 + 测试数据
-- =============================================

-- 1. 创建 product 表
CREATE TABLE IF NOT EXISTS product (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '商品ID',
    name VARCHAR(100) NOT NULL COMMENT '商品名称',
    subtitle VARCHAR(200) COMMENT '副标题/简介',
    price DECIMAL(10,2) NOT NULL COMMENT '售价',
    origin_price DECIMAL(10,2) COMMENT '原价',
    cover_image VARCHAR(500) COMMENT '主图/封面图URL',
    images VARCHAR(2000) COMMENT '轮播图集，逗号分隔',
    category VARCHAR(50) COMMENT '分类',
    tags VARCHAR(200) COMMENT '标签，逗号分隔',
    status INT DEFAULT 1 COMMENT '状态：1-上架 2-下架 3-售罄',
    stock INT DEFAULT 0 COMMENT '库存数量',
    sales INT DEFAULT 0 COMMENT '销量',
    unit VARCHAR(20) COMMENT '单位',
    sort_weight INT DEFAULT 0 COMMENT '排序权重',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品表';

-- 2. 测试数据（分类对齐前端：时令果蔬/地方特色/早餐/速食/肉禽蛋水产/粮油调味）
INSERT INTO product (name, subtitle, price, origin_price, cover_image, images, category, tags, status, stock, sales, unit, sort_weight, created_at, updated_at) VALUES
('有机西红柿', '自然成熟，沙瓤多汁', 9.90, 15.80, 'https://img.example.com/tomato.jpg', 'https://img.example.com/tomato1.jpg,https://img.example.com/tomato2.jpg', '时令果蔬', '有机,当季', 1, 200, 356, '斤', 10, '2026-07-20 08:00:00', '2026-07-29 10:00:00'),
('散养土鸡蛋', '林间散养，蛋黄饱满', 29.90, 39.90, 'https://img.example.com/egg.jpg', 'https://img.example.com/egg1.jpg,https://img.example.com/egg2.jpg', '肉禽蛋水产', '散养,无菌', 1, 150, 218, '盒', 9, '2026-07-21 09:00:00', '2026-07-29 10:00:00'),
('鲜活基围虾', '当日捕捞，活蹦乱跳', 49.90, 65.00, 'https://img.example.com/shrimp.jpg', 'https://img.example.com/shrimp1.jpg,https://img.example.com/shrimp2.jpg', '肉禽蛋水产', '活鲜,高蛋白', 1, 80, 142, '斤', 8, '2026-07-22 07:00:00', '2026-07-29 10:00:00'),
('手工水饺（猪肉白菜）', '手工包制，皮薄馅大', 19.90, 25.90, 'https://img.example.com/dumpling.jpg', 'https://img.example.com/dumpling1.jpg,https://img.example.com/dumpling2.jpg', '速食', '手工,早餐', 1, 300, 521, '袋', 7, '2026-07-23 10:00:00', '2026-07-29 10:00:00'),
('精品牛腩', '谷饲牛肉，肥瘦相间', 55.00, 68.00, 'https://img.example.com/beef.jpg', 'https://img.example.com/beef1.jpg,https://img.example.com/beef2.jpg', '肉禽蛋水产', '谷饲,进口', 1, 60, 89, '斤', 6, '2026-07-24 08:00:00', '2026-07-29 10:00:00'),
('智利车厘子', 'JJ级大果，脆甜多汁', 79.90, 99.00, 'https://img.example.com/cherry.jpg', 'https://img.example.com/cherry1.jpg,https://img.example.com/cherry2.jpg', '时令果蔬', '进口,礼盒', 1, 50, 467, '斤', 5, '2026-07-25 09:00:00', '2026-07-29 10:00:00'),
('三文鱼切片', '挪威进口，刺身级', 89.00, 108.00, 'https://img.example.com/salmon.jpg', 'https://img.example.com/salmon1.jpg,https://img.example.com/salmon2.jpg', '肉禽蛋水产', '进口,刺身', 1, 40, 195, '盒', 4, '2026-07-26 07:00:00', '2026-07-29 10:00:00'),
('有机菠菜', '无农药，嫩叶菠菜', 6.90, 9.90, 'https://img.example.com/spinach.jpg', 'https://img.example.com/spinach1.jpg,https://img.example.com/spinach2.jpg', '时令果蔬', '有机,轻食', 3, 0, 178, '斤', 3, '2026-07-27 08:00:00', '2026-07-29 10:00:00'),
('潮汕牛肉丸', '纯手打，弹牙多汁', 35.00, 45.00, 'https://img.example.com/beefball.jpg', 'https://img.example.com/beefball1.jpg,https://img.example.com/beefball2.jpg', '地方特色', '潮汕,火锅', 1, 180, 634, '袋', 2, '2026-07-28 10:00:00', '2026-07-29 10:00:00'),
('甘肃牛肉面', '正宗兰州风味，汤鲜面劲', 39.90, 49.90, 'https://img.example.com/noodle.jpg', 'https://img.example.com/noodle1.jpg,https://img.example.com/noodle2.jpg', '地方特色', '甘肃,面食', 1, 120, 432, '袋', 1, '2026-07-29 06:00:00', '2026-07-29 10:00:00'),
('新鲜玉米', '甜糯黄玉米，产地直发', 5.90, 8.80, 'https://img.example.com/corn.jpg', 'https://img.example.com/corn1.jpg,https://img.example.com/corn2.jpg', '时令果蔬', '当季,粗粮', 2, 500, 892, '斤', 1, '2026-07-29 06:00:00', '2026-07-29 10:00:00'),
('葱油饼', '层层起酥，外焦里软', 12.90, 18.90, 'https://img.example.com/pancake.jpg', 'https://img.example.com/pancake1.jpg,https://img.example.com/pancake2.jpg', '早餐', '酥脆,快手早餐', 1, 260, 723, '袋', 1, '2026-07-28 07:00:00', '2026-07-29 10:00:00'),
('豆浆油条套装', '传统早餐，现磨豆浆', 15.90, 22.00, 'https://img.example.com/soymilk.jpg', 'https://img.example.com/soymilk1.jpg,https://img.example.com/soymilk2.jpg', '早餐', '传统,营养', 1, 190, 567, '套', 1, '2026-07-27 06:00:00', '2026-07-29 10:00:00'),
('五常大米', '东北稻花香，颗粒分明', 45.00, 58.00, 'https://img.example.com/rice.jpg', 'https://img.example.com/rice1.jpg,https://img.example.com/rice2.jpg', '粮油调味', '五常,东北', 1, 100, 389, '袋', 1, '2026-07-26 10:00:00', '2026-07-29 10:00:00'),
('鲁花花生油', '物理压榨，浓香型', 89.90, 109.00, 'https://img.example.com/oil.jpg', 'https://img.example.com/oil1.jpg,https://img.example.com/oil2.jpg', '粮油调味', '压榨,非转基因', 1, 70, 276, '桶', 1, '2026-07-25 08:00:00', '2026-07-29 10:00:00');
