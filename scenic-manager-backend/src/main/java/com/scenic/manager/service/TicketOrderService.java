package com.scenic.manager.service;

import com.scenic.manager.common.PageResult;
import com.scenic.manager.dto.OrderCreateDTO;
import com.scenic.manager.entity.TicketOrder;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * 订单服务接口
 */
public interface TicketOrderService {
    /**
     * 创建订单
     * 
     * @param dto 订单创建DTO
     * @return 订单信息
     */
    TicketOrder create(OrderCreateDTO dto);

    /**
     * 分页查询订单
     * 
     * @param pageNum 页码
     * @param pageSize 每页大小
     * @param orderNo 订单号（模糊查询）
     * @param userPhone 手机号（模糊查询）
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param orderStatus 订单状态
     * @param scenicId 景区ID
     * @param ticketTypeId 票种ID
     * @return 分页结果
     */
    PageResult<TicketOrder> list(Integer pageNum, Integer pageSize, String orderNo, String userPhone,
                                 LocalDateTime startTime, LocalDateTime endTime, Integer orderStatus,
                                 Long scenicId, Long ticketTypeId);

    /**
     * 修改订单（仅允许修改购票人信息）
     * 
     * @param order 订单信息
     */
    void update(TicketOrder order);

    /**
     * 删除订单（仅允许删除已取消状态的订单）
     * 
     * @param id 订单ID
     */
    void delete(Long id);

    /**
     * 修改订单状态
     * 
     * @param id 订单ID
     * @param orderStatus 新状态
     */
    void updateStatus(Long id, Integer orderStatus);

    /**
     * 订单统计
     * 
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 统计结果
     */
    Map<String, Object> statistics(LocalDateTime startTime, LocalDateTime endTime);

    /**
     * 根据ID查询订单
     * 
     * @param id 订单ID
     * @return 订单信息
     */
    TicketOrder getById(Long id);
}
