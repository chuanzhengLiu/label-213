package com.scenic.manager.controller;

import com.scenic.manager.common.PageResult;
import com.scenic.manager.common.Result;
import com.scenic.manager.dto.OrderCreateDTO;
import com.scenic.manager.entity.TicketOrder;
import com.scenic.manager.service.TicketOrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * 订单管理控制器
 */
@Tag(name = "订单管理")
@RestController
@RequestMapping("/api/order")
@RequiredArgsConstructor
public class TicketOrderController {
    
    private final TicketOrderService ticketOrderService;

    /**
     * 创建订单
     * POST /api/order/create
     */
    @Operation(summary = "创建订单")
    @PostMapping("/create")
    public Result<TicketOrder> create(@Validated @RequestBody OrderCreateDTO dto) {
        try {
            TicketOrder order = ticketOrderService.create(dto);
            return Result.success("下单成功", order);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 分页查询订单
     * GET /api/order/list
     */
    @Operation(summary = "分页查询订单")
    @GetMapping("/list")
    public Result<PageResult<TicketOrder>> list(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String orderNo,
            @RequestParam(required = false) String userPhone,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startTime,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endTime,
            @RequestParam(required = false) Integer orderStatus,
            @RequestParam(required = false) Long scenicId,
            @RequestParam(required = false) Long ticketTypeId) {
        PageResult<TicketOrder> result = ticketOrderService.list(pageNum, pageSize, orderNo, userPhone, 
                startTime, endTime, orderStatus, scenicId, ticketTypeId);
        return Result.success(result);
    }

    /**
     * 修改订单（仅允许修改购票人信息）
     * PUT /api/order/update
     */
    @Operation(summary = "修改订单")
    @PutMapping("/update")
    public Result<?> update(@RequestBody TicketOrder order) {
        try {
            ticketOrderService.update(order);
            return Result.success("修改订单信息成功", null);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 删除订单
     * DELETE /api/order/delete/{id}
     */
    @Operation(summary = "删除订单")
    @DeleteMapping("/delete/{id}")
    public Result<?> delete(@PathVariable Long id) {
        try {
            ticketOrderService.delete(id);
            return Result.success("删除订单成功", null);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 修改订单状态
     * PUT /api/order/updateStatus
     */
    @Operation(summary = "修改订单状态")
    @PutMapping("/updateStatus")
    public Result<?> updateStatus(@RequestParam Long id, @RequestParam Integer orderStatus) {
        try {
            ticketOrderService.updateStatus(id, orderStatus);
            String msg = orderStatus == 2 ? "核销成功" : "取消成功";
            return Result.success(msg, null);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 订单统计
     * GET /api/order/statistics
     */
    @Operation(summary = "订单统计")
    @GetMapping("/statistics")
    public Result<Map<String, Object>> statistics(
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startTime,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endTime) {
        Map<String, Object> result = ticketOrderService.statistics(startTime, endTime);
        return Result.success(result);
    }
}
