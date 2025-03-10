package com.hmall.user.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hmall.user.domain.dto.LoginFormDTO;
import com.hmall.user.domain.dto.UserEditDTO;
import com.hmall.user.domain.dto.UserRoleDto;
import com.hmall.user.domain.po.User;
import com.hmall.user.domain.vo.UserLoginVO;
import com.hmall.user.domain.dto.UserRegisterDTO;
import com.hmall.user.enums.UserRoles;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * <p>
 * 用户表 服务类
 * </p>
 *
 * @author 虎哥
 * @since 2023-05-05
 */
public interface IUserService extends IService<User> {

    UserLoginVO login(LoginFormDTO loginFormDTO);

    void deductMoney(String pw, Integer totalFee);

    void register(UserRegisterDTO userRegister);

    void updateUserById(UserEditDTO userEdit);

    Set<String> queryUserPermissions(Long userId);

    List<String> queryUserRoles(Long userId);

    void updateUserRoles(UserRoleDto userRolesDto);
}
