package com.hmall.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hmall.user.domain.dto.UserRoleDto;
import com.hmall.user.domain.po.User;
import com.hmall.user.enums.UserRoles;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * <p>
 * 用户表 Mapper 接口
 * </p>
 *
 * @author 虎哥
 * @since 2023-05-05
 */
public interface UserMapper extends BaseMapper<User> {
    @Update("update user set balance = balance - ${totalFee} where id = #{userId}")
    void updateMoney(@Param("userId") Long userId, @Param("totalFee") Integer totalFee);

    List<String> queryUserRoles(@Param("userId") Long userId);

    void updateUserRoles(@Param("userId") Long userId, @Param("roles") List<UserRoles> roles);
    @Delete("delete from tb_user_role where user_id = #{userId}")
    void deleteByUserId(@Param("userId") Long userId);
}
