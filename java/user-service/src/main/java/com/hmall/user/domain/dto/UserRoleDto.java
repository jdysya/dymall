package com.hmall.user.domain.dto;

import com.hmall.user.enums.UserRoles;
import com.hmall.user.enums.UserStatus;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@ApiModel(description = "用户角色实体")
public class UserRoleDto {
    @ApiModelProperty(value = "用户ID", required = true)
    private Long userId;
    @ApiModelProperty(value = "用户角色", required = true,allowableValues = "SuperAdmin,ItemAdmin,OrderAdmin,Guest")
    private List<UserRoles> userRoles;
}
