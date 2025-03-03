package com.hmall.user.controller;

import com.hmall.common.annotation.RequirePermission;
import com.hmall.user.domain.dto.LoginFormDTO;
import com.hmall.user.domain.dto.UserRoleDto;
import com.hmall.user.domain.po.User;
import com.hmall.user.domain.dto.UserEditDTO;
import com.hmall.user.domain.vo.UserLoginVO;
import com.hmall.user.domain.dto.UserRegisterDTO;
import com.hmall.user.enums.UserRoles;
import com.hmall.user.service.IUserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Api(tags = "用户相关接口")
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final IUserService userService;

    @ApiOperation("用户登录接口")
    @PostMapping("login")
    public UserLoginVO login(@RequestBody @Validated LoginFormDTO loginFormDTO){
        return userService.login(loginFormDTO);
    }

    @ApiOperation("扣减余额")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pw", value = "支付密码"),
            @ApiImplicitParam(name = "amount", value = "支付金额")
    })
    @PutMapping("/money/deduct")
    @RequirePermission({"Guest","OrderAdmin","SuperAdmin"})
    public void deductMoney(@RequestParam("pw") String pw,@RequestParam("amount") Integer amount){
        userService.deductMoney(pw, amount);
    }
    @ApiOperation("用户注册")
    @PostMapping("/register")
    public void register(@RequestBody @Validated UserRegisterDTO userRegister){
        userService.register(userRegister);
    }
    @ApiOperation("根据id删除用户")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "用户id",required = true),
    })
    @DeleteMapping("/{id}")
    @RequirePermission({"SuperAdmin"})
    public void deleteUserById(@PathVariable("id") Long id){
        userService.removeById(id);
    }
    @ApiOperation("更新用户")
    @PostMapping("/update")
    @RequirePermission({"Guest","SuperAdmin"})
    public void updateUser(@RequestBody UserEditDTO userEdit){
        userService.updateUserById(userEdit);
    }
    @ApiOperation("根据id查询用户信息")
    @RequirePermission({"ItemAdmin","OrderAdmin","SuperAdmin"})
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "用户id",required = true),
    })
    @GetMapping("/{id}")
    public User getUserById(@PathVariable Long id){
        return userService.getById(id);
    }

    @ApiOperation("根据id查询用户角色id")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "用户id",required = true),
    })
    @GetMapping("/roles")
    public List<String> queryUserRoles(@RequestParam("userId") Long userId) {
        return userService.queryUserRoles(userId);
    }
    @ApiOperation("修改用户角色")
    @RequirePermission({"SuperAdmin"})
    @PostMapping("/roles")
    public void updateUserRoles(@RequestBody UserRoleDto userRolesDto) {
        userService.updateUserRoles(userRolesDto);
    }
}

