package com.hmall.common.aspect;

import com.hmall.common.annotation.RequirePermission;
import com.hmall.common.utils.UserContext;
import org.aspectj.lang.ProceedingJoinPoint;
import org.springframework.web.context.request.RequestContextHolder;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import javax.servlet.http.HttpServletResponse;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Set;

@Aspect
@Component
public class PermissionAspect {

    @Around("@annotation(requirePermission)")
    public Object checkPermission(ProceedingJoinPoint joinPoint, RequirePermission requirePermission) throws Throwable {
        // 获取当前请求的 ServerWebExchange
        String userRole = UserContext.getRole();

        // 从请求头中获取用户信息和角色
    //    ServerWebExchange exchange = (ServerWebExchange) RequestContextHolder.getRequestAttributes();
//        String userInfo = exchange.getRequest().getHeaders().getFirst("user-info");
//        String userRole = exchange.getRequest().getHeaders().getFirst("user-role");
//        System.out.println("userInfo: " + userInfo);
        System.out.println("userRole: " + userRole);
        // 权限校验逻辑
        if (userRole == null) {
            return null;
        }

        if (userRole.equals("SuperAdmin")) { // 1号是超级管理员直接跳过权限验证
            return joinPoint.proceed();
        }

        // 将 userRole 字符串按逗号分割成数组
        String[] userRoleArray = userRole.split(",");
        // 创建一个 HashSet 来存储分割后的角色
        Set<String> userRoleSet = new HashSet<>();
        for (String role : userRoleArray) {
            // 去除角色前后可能存在的空格
            userRoleSet.add(role.trim());
        }

        // 示例：简单权限校验
        String[] requiredPermission = requirePermission.value();

        // 对比角色
        if (hasMatchingRole(userRoleSet, requiredPermission)) {
            return joinPoint.proceed();
        }

        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes != null) {
            HttpServletResponse response = attributes.getResponse();
            if (response != null) {
                response.setStatus(HttpStatus.FORBIDDEN.value());
                response.getWriter().write("Forbidden: Insufficient permissions");
                return null; // 直接返回，不再执行目标方法
            }
        }
        throw new SecurityException("Insufficient permissions to access this resource");
    }

    /**
     * 检查是否有匹配的角色
     * @param userRoleSet 用户角色集合
     * @param requiredPermission 需要的权限数组
     * @return 是否有匹配的角色
     */
    private boolean hasMatchingRole(Set<String> userRoleSet, String[] requiredPermission) {
        for (String requiredRole : requiredPermission) {
            if (userRoleSet.contains(requiredRole)) {
                return true;
            }
        }
        return false;
    }

}