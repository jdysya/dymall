package com.hmall.user.domain.dto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.hmall.user.enums.UserStatus;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.validation.constraints.Size;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 用户表
 * </p>
 *
 * @author 虎哥
 * @since 2023-05-05
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("user")
public class UserEditDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    @ApiModelProperty(value = "用户id", required = true,position = 1)
    private Long id;

    /**
     * 用户名
     */
    @ApiModelProperty(value = "用户名", position = 2)
    private String username;

    /**
     * 密码，加密存储
     */
    @ApiModelProperty(value = "密码", position = 3)
    private String password;

    /**
     * 注册手机号
     */
    @ApiModelProperty(value = "手机号", position = 4)
    @Size(min = 11, max = 11, message = "手机号必须是11位")
    private String phone;

    /**
     * 创建时间
     */
    @JsonIgnore
    private LocalDateTime updateTime;

    /**
     * 使用状态（1正常 2冻结）
     */
    @ApiModelProperty(value = "状态", position = 5,allowableValues = "NORMAL, FROZEN")
    private UserStatus status;



}
