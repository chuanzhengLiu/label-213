package com.scenic.manager.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.scenic.manager.common.PageResult;
import com.scenic.manager.dto.OrderCreateDTO;
import com.scenic.manager.entity.TicketOrder;
import com.scenic.manager.entity.TicketType;
import com.scenic.manager.mapper.TicketOrderMapper;
import com.scenic.manager.mapper.TicketTypeMapper;
import com.scenic.manager.service.TicketOrderService;
import com.scenic.manager.utils.OrderNoUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 订单服务实现类
 */
@Service
@RequiredArgsConstructor
public class TicketOrderServiceImpl implements TicketOrderService {
    
    private final TicketOrderMapper ticketOrderMapper;
    private final TicketTypeMapper ticketTypeMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public TicketOrder create(OrderCreateDTO dto) {
        // 查询票种信息
        TicketType ticketType = ticketTypeMapper.selectById(dto.getTicketTypeId());
        if (ticketType == null) {
            throw new RuntimeException("票种不存在");
        }

        // 校验库存
        if (ticketType.getStock() < dto.getTicketNum()) {
            throw new RuntimeException("库存不足，当前剩余" + ticketType.getStock() + "张");
        }

        // 使用乐观锁扣减库存
        LambdaUpdateWrapper<TicketType> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(TicketType::getId, dto.getTicketTypeId())
                .eq(TicketType::getVersion, ticketType.getVersion())
                .set(TicketType::getStock, ticketType.getStock() - dto.getTicketNum())
                .set(TicketType::getVersion, ticketType.getVersion() + 1)
                .set(TicketType::getUpdateTime, LocalDateTime.now());
        
        int updateCount = ticketTypeMapper.update(null, updateWrapper);
        if (updateCount == 0) {
            throw new RuntimeException("库存扣减失败，请重试");
        }

        // 生成订单编号
        String orderNo = OrderNoUtil.generateOrderNo();

        // 计算订单总金额
        BigDecimal totalAmount = ticketType.getPrice().multiply(new BigDecimal(dto.getTicketNum()));

        // 创建订单
        TicketOrder order = new TicketOrder();
        order.setOrderNo(orderNo);
        order.setScenicId(dto.getScenicId());
        order.setTicketTypeId(dto.getTicketTypeId());
        order.setUserName(dto.getUserName());
        order.setUserPhone(dto.getUserPhone());
        order.setTicketNum(dto.getTicketNum());
        order.setTotalAmount(totalAmount);
        order.setOrderStatus(1); // 1-已下单
        order.setCreateTime(LocalDateTime.now());
        order.setUpdateTime(LocalDateTime.now());

        ticketOrderMapper.insert(order);
        return order;
    }

    @Override
    public PageResult<TicketOrder> list(Integer pageNum, Integer pageSize, String orderNo, String userPhone,
                                        LocalDateTime startTime, LocalDateTime endTime, Integer orderStatus,
                                        Long scenicId, Long ticketTypeId) {
        Page<TicketOrder> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<TicketOrder> wrapper = new LambdaQueryWrapper<>();
        
        if (orderNo != null && !orderNo.isEmpty()) {
            wrapper.like(TicketOrder::getOrderNo, orderNo);
        }
        if (userPhone != null && !userPhone.isEmpty()) {
            wrapper.like(TicketOrder::getUserPhone, userPhone);
        }
        if (startTime != null) {
            wrapper.ge(TicketOrder::getCreateTime, startTime);
        }
        if (endTime != null) {
            wrapper.le(TicketOrder::getCreateTime, endTime);
        }
        if (orderStatus != null) {
            wrapper.eq(TicketOrder::getOrderStatus, orderStatus);
        }
        if (scenicId != null) {
            wrapper.eq(TicketOrder::getScenicId, scenicId);
        }
        if (ticketTypeId != null) {
            wrapper.eq(TicketOrder::getTicketTypeId, ticketTypeId);
        }
        
        wrapper.orderByDesc(TicketOrder::getCreateTime);
        Page<TicketOrder> result = ticketOrderMapper.selectPage(page, wrapper);
        
        return new PageResult<>(result.getTotal(), result.getRecords());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(TicketOrder order) {
        TicketOrder existOrder = getById(order.getId());
        if (existOrder == null) {
            throw new RuntimeException("订单不存在");
        }

        // 仅允许修改购票人信息
        if (order.getUserName() != null) {
            existOrder.setUserName(order.getUserName());
        }
        if (order.getUserPhone() != null) {
            existOrder.setUserPhone(order.getUserPhone());
        }

        existOrder.setUpdateTime(LocalDateTime.now());
        ticketOrderMapper.updateById(existOrder);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        TicketOrder order = getById(id);
        if (order == null) {
            throw new RuntimeException("订单不存在");
        }

        // 仅允许删除已取消状态的订单
        if (order.getOrderStatus() != 3) {
            throw new RuntimeException("仅允许删除已取消状态的订单");
        }

        ticketOrderMapper.deleteById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateStatus(Long id, Integer orderStatus) {
        TicketOrder order = getById(id);
        if (order == null) {
            throw new RuntimeException("订单不存在");
        }

        // 仅状态1可修改为2/3
        if (order.getOrderStatus() != 1) {
            throw new RuntimeException("仅已下单状态的订单可修改状态");
        }

        if (orderStatus != 2 && orderStatus != 3) {
            throw new RuntimeException("订单状态只能修改为2（已核销）或3（已取消）");
        }

        // 如果修改为已取消，恢复票种库存
        if (orderStatus == 3) {
            TicketType ticketType = ticketTypeMapper.selectById(order.getTicketTypeId());
            if (ticketType != null) {
                LambdaUpdateWrapper<TicketType> updateWrapper = new LambdaUpdateWrapper<>();
                updateWrapper.eq(TicketType::getId, order.getTicketTypeId())
                        .set(TicketType::getStock, ticketType.getStock() + order.getTicketNum())
                        .set(TicketType::getUpdateTime, LocalDateTime.now());
                ticketTypeMapper.update(null, updateWrapper);
            }
        }

        order.setOrderStatus(orderStatus);
        order.setUpdateTime(LocalDateTime.now());
        ticketOrderMapper.updateById(order);
    }

    @Override
    public Map<String, Object> statistics(LocalDateTime startTime, LocalDateTime endTime) {
        LambdaQueryWrapper<TicketOrder> wrapper = new LambdaQueryWrapper<>();
        
        if (startTime != null) {
            wrapper.ge(TicketOrder::getCreateTime, startTime);
        }
        if (endTime != null) {
            wrapper.le(TicketOrder::getCreateTime, endTime);
        }
        // 只统计已核销的订单
        wrapper.eq(TicketOrder::getOrderStatus, 2);
        
        List<TicketOrder> orders = ticketOrderMapper.selectList(wrapper);

        // 计算总销售额和总销量
        BigDecimal totalSales = orders.stream()
                .map(TicketOrder::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        Integer totalNum = orders.stream()
                .mapToInt(TicketOrder::getTicketNum)
                .sum();

        // 票种销量排行
        Map<Long, Integer> ticketTypeCountMap = orders.stream()
                .collect(Collectors.groupingBy(
                        TicketOrder::getTicketTypeId,
                        Collectors.summingInt(TicketOrder::getTicketNum)
                ));

        List<Map<String, Object>> ticketTypeRank = ticketTypeCountMap.entrySet().stream()
                .sorted((e1, e2) -> e2.getValue().compareTo(e1.getValue()))
                .limit(10)
                .map(entry -> {
                    Map<String, Object> item = new HashMap<>();
                    TicketType ticketType = ticketTypeMapper.selectById(entry.getKey());
                    item.put("ticketTypeId", entry.getKey());
                    item.put("ticketTypeName", ticketType != null ? ticketType.getTypeName() : "未知");
                    item.put("salesCount", entry.getValue());
                    return item;
                })
                .collect(Collectors.toList());

        Map<String, Object> result = new HashMap<>();
        result.put("totalSales", totalSales);
        result.put("totalNum", totalNum);
        result.put("ticketTypeRank", ticketTypeRank);

        return result;
    }

    @Override
    public TicketOrder getById(Long id) {
        return ticketOrderMapper.selectById(id);
    }
}
