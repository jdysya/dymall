package com.hmall.api.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

@FeignClient("user-service")
public interface UserClient {
    @GetMapping("/users/permissions")
    Set<String> queryUserPermissions(@RequestParam("userId") Long userId);

    @GetMapping("/users/roles")
    List<String> queryUserRoles(@RequestParam("userId") Long userId);

    @PutMapping("/users/money/deduct")
    void deductMoney(@RequestParam("pw") String pw,@RequestParam("amount") Integer amount);

//    @GetMapping("/*")
//    void getUserRoles(@RequestParam("userId") Long userId);
}