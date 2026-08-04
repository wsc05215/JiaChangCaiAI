package com.jcx.jiachangcai.module.user.adress.entity;

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
 * 收货地址表
 * </p>
 *
 * @author wsc
 * @since 2026-08-03
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("address")
@Schema(description = "收货地址表")
public class Address implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "地址主键")
    @TableId(value = "address_id", type = IdType.AUTO)
    private Long addressId;

    @Schema(description = "用户ID，关联user表")
    private Long userId;

    @Schema(description = "收货人姓名")
    private String receiver;

    @Schema(description = "收货人手机号")
    private String phone;

    @Schema(description = "省")
    private String province;

    @Schema(description = "市")
    private String city;

    @Schema(description = "区/县")
    private String district;

    @Schema(description = "详细地址")
    private String detail;

    @Schema(description = "是否默认 1是 0否")
    private Integer isDefault;

    @Schema(description = "0正常 1已删")
    private Integer isDeleted;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    private LocalDateTime updateTime;

}
