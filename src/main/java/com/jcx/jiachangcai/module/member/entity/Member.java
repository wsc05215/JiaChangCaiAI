package com.jcx.jiachangcai.module.member.entity;

import java.math.BigDecimal;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.time.LocalDateTime;
import java.io.Serializable;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 会员表
 * </p>
 *
 * @author wsc
 * @since 2026-07-30
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("member")
@Schema(description = "会员表")
public class Member implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description ="会员记录主键")
    @TableId(value = "member_id", type = IdType.AUTO)
    private Long memberId;

    @Schema(description ="关联用户ID")
    private Long userId;

    @Schema(description ="会员等级称号 如：超级大厨神、美食家等")
    private String memberLevel;

    @Schema(description ="会员类型 1-食谱定制会员 2-食材管理会员 3-尊享会员")
    private Integer memberType;

    @Schema(description ="会员生效时间")
    private LocalDateTime startTime;

    @Schema(description ="会员到期时间")
    private LocalDateTime expireTime;

    @Schema(description ="实付金额(元)")
    private BigDecimal amount;

    @Schema(description ="原价金额(元)，用于展示划线价")
    private BigDecimal originalAmount;

    @Schema(description ="支付渠道 wxpay/alipay")
    private String payChannel;

    @Schema(description ="第三方支付流水号")
    private String transactionId;

    @Schema(description ="陪吃顿饭数累计")
    private Integer mealCount;

    @Schema(description ="状态 0-已过期 1-使用中 2-已退款")
    private Integer status;

    @Schema(description ="逻辑删除 0-正常 1-已删除")
    private Integer isDeleted;

    @Schema(description ="创建时间")
    private LocalDateTime createTime;

    @Schema(description ="更新时间")
    private LocalDateTime updateTime;

    @Schema(description ="备注")
    private String remark;

}
