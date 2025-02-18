package com.hmall.trade.domain.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
//@NoArgsConstructor 不知道为啥这两没生效
//@AllArgsConstructor
@ApiModel(description = "订单表单实体")
public class OrderEditDTO {
    @ApiModelProperty(value = "订单id",required = true)
    private Long id;
    @ApiModelProperty("总金额，单位为分，取值大于等于0")
    private Integer totalFee;
    @ApiModelProperty("支付类型，1、支付宝，2、微信，3、扣减余额")
    private Integer paymentType;
    @ApiModelProperty("用户id")
    private Long userId;
    @ApiModelProperty("订单的状态，1、未付款 2、已付款,未发货 3、已发货,未确认 4、确认收货，交易成功 5、交易取消，订单关闭 6、交易结束，已评价")
    private Integer status;
    @ApiModelProperty("创建时间")
    private LocalDateTime createTime;
    @ApiModelProperty("支付时间")
    private LocalDateTime payTime;
    @ApiModelProperty("发货时间")
    private LocalDateTime consignTime;
    @ApiModelProperty("交易完成时间")
    private LocalDateTime endTime;
    @ApiModelProperty("交易关闭时间")
    private LocalDateTime closeTime;
    @ApiModelProperty("评价时间")
    private LocalDateTime commentTime;
    @JsonIgnore
    private LocalDateTime updateTime;
    // 无参构造函数
    public OrderEditDTO() {
    }

    // 全参构造函数
    public OrderEditDTO(Long id, Integer totalFee, Integer paymentType, Long userId, Integer status,
                        LocalDateTime createTime, LocalDateTime payTime, LocalDateTime consignTime,
                        LocalDateTime endTime, LocalDateTime closeTime, LocalDateTime commentTime,
                        LocalDateTime updateTime) {
        this.id = id;
        this.totalFee = totalFee;
        this.paymentType = paymentType;
        this.userId = userId;
        this.status = status;
        this.createTime = createTime;
        this.payTime = payTime;
        this.consignTime = consignTime;
        this.endTime = endTime;
        this.closeTime = closeTime;
        this.commentTime = commentTime;
        this.updateTime = updateTime;
    }
}
