package com.hmall.gateway.filter;

import com.hmall.api.client.UserClient;
import com.hmall.common.exception.UnauthorizedException;
import com.hmall.common.utils.CollUtils;
import com.hmall.gateway.config.AuthProperties;
import com.hmall.gateway.utils.JwtTool;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
@EnableConfigurationProperties(AuthProperties.class)
public class AuthGlobalFilter implements GlobalFilter, Ordered {

    private final JwtTool jwtTool;

    private final UserClient userClient;

    private final AuthProperties authProperties;

    private final AntPathMatcher antPathMatcher = new AntPathMatcher();

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        // 1.获取Request
        ServerHttpRequest request = exchange.getRequest();
        // 用于判断白名单
        String path = request.getPath().toString();
        String method = request.getMethodValue();
        // 2.判断是否不需要拦截
        if (isExclude(path, method)) {
            // 无需拦截，直接放行
            return chain.filter(exchange)
                    .contextWrite(context -> context.put(ServerWebExchange.class, exchange));
        }
        // 3.获取请求头中的token
        String token = null;
        List<String> headers = request.getHeaders().get("authorization");
        if (!CollUtils.isEmpty(headers)) {
            token = headers.get(0);
        }
        // 4.校验并解析token
        Long userId = null;
        try {
            userId = jwtTool.parseToken(token);
        } catch (UnauthorizedException e) {
            // 如果无效，拦截
            ServerHttpResponse response = exchange.getResponse();
            response.setRawStatusCode(401);
            return response.setComplete();
        }

//        // 5.获取路径对应的权限注解
//        String requiredPermission = getRequiredPermission(path, exchange);
//        if (StringUtils.hasText(requiredPermission)) {
//            // 6.远程调用判断权限
//            Long finalUserId = userId;
//            return Mono.fromCallable(() -> {
//                        Set<String> permissions = userClient.queryUserPermissions(finalUserId);
//                        return hasPermission(permissions, requiredPermission);
//                    }).subscribeOn(Schedulers.boundedElastic())
//                    .flatMap(hasPermission -> {
//                        if (hasPermission) {
//                            // 7.如果有效，传递用户信息
//                            String userInfo = finalUserId.toString();
//                            ServerWebExchange ex = exchange.mutate()
//                                    .request(r -> r.header("user-info", userInfo))
//                                    .build();
//                            return chain.filter(ex);
//                        } else {
//                            return handleNoPermission(exchange);
//                        }
//                    });
//        }
        // 8.如果没有权限要求，直接放行
        String userInfo = userId.toString();
        // 根据user_id去数据库查询roleid和能访问的路径path
//        Map<Long, Arrays> roleIdPermission = userClient.queryUserRoles(userId);
        System.out.println(userId);
        List<String> userRoles = userClient.queryUserRoles(userId);
        if (userRoles == null) {
            userRoles = List.of();
        }
        List<String> finalUserRoles = userRoles;
        ServerWebExchange ex = exchange.mutate()
                .request(r -> r
                        .header("user-info", userInfo)
                        .header("user-role",  String.join(",", finalUserRoles)))
                .build();
        return chain.filter(ex)
                .contextWrite(context -> context.put(ServerWebExchange.class, exchange));
    }

//    private boolean isExclude(String antPath) {
//        for (String pathPattern : authProperties.getExcludePaths()) {
//            if (antPathMatcher.match(pathPattern, antPath)) {
//                return true;
//            }
//        }
//        return false;
//    }
    private boolean isExclude(String antPath, String method) {
        // 检查 excludePaths
        for (String excludePath : authProperties.getExcludePaths()) {
            if (antPathMatcher.match(excludePath, antPath)) {
                return true;
            }
        }

        // 检查 whiteList
        for (AuthProperties.WhiteListEntry entry : authProperties.getWhiteList()) {
            if (antPathMatcher.match(entry.getPath(), antPath) && entry.getMethods().contains(method)) {
                return true;
            }
        }
        return false;
    }
//    private String getRequiredPermission(String path, ServerWebExchange exchange) {
//        // Create a map with key as "METHOD:path" and value as permission
//        Map<String, String> permissionMapping = new HashMap<>();
//
//        // Orders
//        permissionMapping.put("POST:/orders", "order:create");
//        permissionMapping.put("PUT:/orders/update", "order:update");
//        permissionMapping.put("GET:/orders/{id}", "order:query");
//        permissionMapping.put("PUT:/orders/{id}", "order:complete");
//
//        // Addresses
//        permissionMapping.put("GET:/addresses", "address:queryAll");
//        permissionMapping.put("GET:/addresses/{addressId}", "address:queryById");
//
//        // Users
//        permissionMapping.put("PUT:/users/money/deduct", "user:updateMoney");
//        permissionMapping.put("PUT:/users/update", "user:update");
//        permissionMapping.put("GET:/users/{id}", "user:query");
//        permissionMapping.put("DELETE:/users/{id}", "user:delete");
//
//        // cart
//        permissionMapping.put("GET:/carts", "cart:query");
//        permissionMapping.put("POST:/carts", "cart:add");
//        permissionMapping.put("PUT:/carts", "cart:update");
//        permissionMapping.put("DELETE:/carts/{ids}", "cart:delete");
//        permissionMapping.put("DELETE:/carts/{id}", "cart:delete");
//
//
//        // Items
//        permissionMapping.put("GET:/items", "item:query");
//        permissionMapping.put("GET:/items/page", "item:query");
//        permissionMapping.put("GET:/items/{id}", "item:query");
//        permissionMapping.put("POST:/items", "item:add");
//        permissionMapping.put("PUT:/items", "item:update");
//        permissionMapping.put("PUT:/items/status/{id}/{status}", "item:update");
//        permissionMapping.put("PUT:/items/stock/deduct", "item:update");
//        permissionMapping.put("DELETE:/items/{id}", "item:delete");
//
//        // pay
//        permissionMapping.put("POST:/pay-orders", "pay:create");
//        permissionMapping.put("POST:/pay-orders/{id}", "pay:update");
//
//        // Get the request method from the current request
//        String method = exchange.getRequest().getMethodValue();
//        String key = method + ":" + path;
//
//        return permissionMapping.get(key);
//    }
//
//    private boolean hasPermission(Set<String> permissions, String required) {
//        // 超级管理员权限判断
//        if (permissions.contains("*:*")) {
//            return true;
//        }
//
//        // 通配符权限判断
//        for (String permission : permissions) {
//            if (permission.endsWith("*")) {
//                String pattern = permission.replace("*", ".*");
//                if (Pattern.matches(pattern, required)) {
//                    return true;
//                }
//            }
//        }
//
//        // 精确权限判断
//        return permissions.contains(required);
//    }
    private Mono<Void> handleNoAuth(ServerWebExchange exchange) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        return response.writeWith(Mono.just(response.bufferFactory()
                .wrap("未登录".getBytes(StandardCharsets.UTF_8))));
    }

//    private Mono<Void> handleNoPermission(ServerWebExchange exchange) {
//        ServerHttpResponse response = exchange.getResponse();
//        response.setStatusCode(HttpStatus.FORBIDDEN);
//        return response.writeWith(Mono.just(response.bufferFactory()
//                .wrap("无权限访问".getBytes(StandardCharsets.UTF_8))));
//    }
    @Override
    public int getOrder() {
        return 0;
    }
}