package com.hmall.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hmall.common.exception.BadRequestException;
import com.hmall.common.exception.BizIllegalException;
import com.hmall.common.exception.ForbiddenException;
import com.hmall.common.utils.UserContext;
import com.hmall.user.config.JwtProperties;
import com.hmall.user.domain.dto.LoginFormDTO;
import com.hmall.user.domain.dto.UserRoleDto;
import com.hmall.user.domain.po.User;
import com.hmall.user.domain.dto.UserEditDTO;
import com.hmall.user.domain.vo.UserLoginVO;
import com.hmall.user.domain.dto.UserRegisterDTO;
import com.hmall.user.enums.UserRoles;
import com.hmall.user.enums.UserStatus;
import com.hmall.user.mapper.UserMapper;
import com.hmall.user.service.IUserService;
import com.hmall.user.utils.JwtTool;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * <p>
 * 用户表 服务实现类
 * </p>
 *
 * @author 虎哥
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {

    private final PasswordEncoder passwordEncoder;

    private final JwtTool jwtTool;

    private final JwtProperties jwtProperties;
    private final UserMapper userMapper;

    @Override
    public UserLoginVO login(LoginFormDTO loginDTO) {
        // 1.数据校验
        String username = loginDTO.getUsername();
        String password = loginDTO.getPassword();
        // 2.根据用户名或手机号查询
        User user = lambdaQuery().eq(User::getUsername, username).one();
        Assert.notNull(user, "用户名错误");
        // 3.校验是否禁用
        if (user.getStatus() == UserStatus.FROZEN) {
            throw new ForbiddenException("用户被冻结");
        }
        // 4.校验密码
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new BadRequestException("用户名或密码错误");
        }
        // 5.生成TOKEN
        String token = jwtTool.createToken(user.getId(), jwtProperties.getTokenTTL());
        // 6.封装VO返回
        UserLoginVO vo = new UserLoginVO();
        vo.setUserId(user.getId());
        vo.setUsername(user.getUsername());
        vo.setBalance(user.getBalance());
        vo.setToken(token);
        return vo;
    }

    @Override
    public void deductMoney(String pw, Integer totalFee) {
        log.info("开始扣款");
        // 1.校验密码
        User user = getById(UserContext.getUser());
        if(user == null || !passwordEncoder.matches(pw, user.getPassword())){
            // 密码错误
            throw new BizIllegalException("用户密码错误");
        }

        // 2.尝试扣款
        try {
            baseMapper.updateMoney(UserContext.getUser(), totalFee);
        } catch (Exception e) {
            throw new RuntimeException("扣款失败，可能是余额不足！", e);
        }
        log.info("扣款成功");
    }

    @Override
    public void register(UserRegisterDTO userRegister) {
        userRegister.setBalance(0);
        userRegister.setCreateTime(LocalDateTime.now());
        userRegister.setUpdateTime(LocalDateTime.now());
        userRegister.setPassword(passwordEncoder.encode(userRegister.getPassword()));
        // 创建一个新的 User 对象
        User user = new User();
        BeanUtils.copyProperties(userRegister,user);
        save(user);
    }

    @Override
    public void updateUserById(UserEditDTO userEdit) {
        userEdit.setUpdateTime(LocalDateTime.now());
        User user = new User();
        BeanUtils.copyProperties(userEdit,user);
        // 这里都没验证密码是不是不存在就给她加密不合理
//        user.setPassword(passwordEncoder.encode(userEdit.getPassword()));
        if (userEdit.getPassword() != null && !userEdit.getPassword().isEmpty()){
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        }
        Integer i = null;
        if(user.getStatus() != null){
            i = user.getStatus().getValue();
        }
        LambdaUpdateWrapper<User> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(User::getId, user.getId())
                .set(user.getPhone() != null && !user.getPhone().isEmpty(), User::getPhone, user.getPhone())
                .set(user.getUsername() != null && !user.getUsername().isEmpty(), User::getUsername, user.getUsername())
                .set(user.getPassword() != null && !user.getPassword().isEmpty(), User::getPassword, user.getPassword())
                .set(user.getStatus() !=null, User::getStatus, i)
                .set(User::getUpdateTime, user.getUpdateTime());
        userMapper.update(null, updateWrapper);
    }

    // 还没实现
    @Override
    public Set<String> queryUserPermissions(Long userId) {
        // 获取用户身份信息
        return Set.of();
    }

    @Override
    public List<String> queryUserRoles(Long userId) {
        return userMapper.queryUserRoles(userId);
    }

    @Override
    public void updateUserRoles(UserRoleDto userRoleDto) {
        Long userId = userRoleDto.getUserId();
        List<UserRoles> roles = userRoleDto.getUserRoles();

        // 先删除用户id已有的权限
        userMapper.deleteByUserId(userId);
        // 更新新的权限
        System.out.println(userId);
        System.out.println(roles);
        userMapper.updateUserRoles(userId, roles);
    }
}
