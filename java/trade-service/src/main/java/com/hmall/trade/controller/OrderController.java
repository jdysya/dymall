package com.hmall.trade.controller;

import com.hmall.common.annotation.RequirePermission;
import com.hmall.common.utils.BeanUtils;
import com.hmall.trade.domain.dto.OrderEditDTO;
import com.hmall.trade.domain.dto.OrderFormDTO;
import com.hmall.trade.domain.po.Order;
import com.hmall.trade.domain.vo.OrderVO;
import com.hmall.trade.service.IOrderService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.apache.ibatis.annotations.Param;
import org.springframework.web.bind.annotation.*;

@Api(tags = "订单管理接口")
@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {
    private final IOrderService orderService;

    @ApiOperation("根据id查询订单")
    @GetMapping("{id}")
    @RequirePermission({"Guest","OrderAdmin","SuperAdmin"})
    public OrderVO queryOrderById(@Param ("订单id")@PathVariable("id") Long orderId) {
        return BeanUtils.copyBean(orderService.getById(orderId), OrderVO.class);
    }

    @ApiOperation("创建订单")
    @PostMapping
    @RequirePermission({"Guest","OrderAdmin","SuperAdmin"})
    public Long createOrder(@RequestBody OrderFormDTO orderFormDTO){
        return orderService.createOrder(orderFormDTO);
    }

    @ApiOperation("标记订单已支付")
    @ApiImplicitParam(name = "orderId", value = "订单id", paramType = "path")
    @PutMapping("/{orderId}")
    @RequirePermission({"OrderAdmin","SuperAdmin"})
    public void markOrderPaySuccess(@PathVariable("orderId") Long orderId) {
        orderService.markOrderPaySuccess(orderId);
    }
    @ApiOperation("修改订单信息")
    @PostMapping("/update")
    @RequirePermission({"OrderAdmin","SuperAdmin"})
    public void updateOrder(@RequestBody OrderEditDTO orderEditDTO){
        orderService.updateOrder(orderEditDTO);
    }
}
