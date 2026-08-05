-- 订单退货功能 - 数据库迁移脚本
-- 为 order_item 表添加退货相关字段

ALTER TABLE order_item
    ADD COLUMN received_time  DATETIME     DEFAULT NULL COMMENT '收货时间',
    ADD COLUMN return_status  TINYINT      DEFAULT 0   COMMENT '退货状态：0-未退货 1-退货中 2-已退货',
    ADD COLUMN return_reason  VARCHAR(500) DEFAULT NULL COMMENT '退货原因';