package com.scenic.manager.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.scenic.manager.common.PageResult;
import com.scenic.manager.entity.ScenicSpot;
import com.scenic.manager.entity.TicketOrder;
import com.scenic.manager.entity.TicketType;
import com.scenic.manager.mapper.ScenicSpotMapper;
import com.scenic.manager.mapper.TicketOrderMapper;
import com.scenic.manager.mapper.TicketTypeMapper;
import com.scenic.manager.service.TicketTypeService;
import com.scenic.manager.vo.TicketTypeVO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 票种服务实现类
 */
@Service
@RequiredArgsConstructor
public class TicketTypeServiceImpl implements TicketTypeService {
    
    private final TicketTypeMapper ticketTypeMapper;
    private final ScenicSpotMapper scenicSpotMapper;
    private final TicketOrderMapper ticketOrderMapper;

    @Override
    public PageResult<TicketType> list(Integer pageNum, Integer pageSize, Long scenicId, String typeName, 
                                      BigDecimal priceMin, BigDecimal priceMax) {
        Page<TicketType> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<TicketType> wrapper = new LambdaQueryWrapper<>();
        
        if (scenicId != null) {
            wrapper.eq(TicketType::getScenicId, scenicId);
        }
        if (StringUtils.hasText(typeName)) {
            wrapper.like(TicketType::getTypeName, typeName);
        }
        if (priceMin != null) {
            wrapper.ge(TicketType::getPrice, priceMin);
        }
        if (priceMax != null) {
            wrapper.le(TicketType::getPrice, priceMax);
        }
        
        wrapper.orderByDesc(TicketType::getCreateTime);
        Page<TicketType> result = ticketTypeMapper.selectPage(page, wrapper);
        
        return new PageResult<>(result.getTotal(), result.getRecords());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void add(TicketType ticketType) {
        // 校验景区是否存在
        if (ticketType.getScenicId() == null) {
            throw new RuntimeException("景区ID不能为空");
        }
        ScenicSpot scenicSpot = scenicSpotMapper.selectById(ticketType.getScenicId());
        if (scenicSpot == null) {
            throw new RuntimeException("景区不存在");
        }

        // 校验票种名称
        if (!StringUtils.hasText(ticketType.getTypeName())) {
            throw new RuntimeException("票种名称不能为空");
        }

        // 校验价格
        if (ticketType.getPrice() == null || ticketType.getPrice().compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("价格必须大于0");
        }

        // 校验库存
        if (ticketType.getStock() == null || ticketType.getStock() < 0) {
            throw new RuntimeException("库存必须大于等于0");
        }

        ticketType.setVersion(0);
        ticketType.setCreateTime(LocalDateTime.now());
        ticketType.setUpdateTime(LocalDateTime.now());
        ticketTypeMapper.insert(ticketType);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(TicketType ticketType) {
        TicketType existType = getById(ticketType.getId());
        if (existType == null) {
            throw new RuntimeException("票种不存在");
        }

        // 保存原始的version值，用于乐观锁
        Integer originalVersion = existType.getVersion();

        // 校验景区是否存在
        if (ticketType.getScenicId() != null) {
            ScenicSpot scenicSpot = scenicSpotMapper.selectById(ticketType.getScenicId());
            if (scenicSpot == null) {
                throw new RuntimeException("景区不存在");
            }
            existType.setScenicId(ticketType.getScenicId());
        }

        if (StringUtils.hasText(ticketType.getTypeName())) {
            existType.setTypeName(ticketType.getTypeName());
        }

        // 校验价格
        if (ticketType.getPrice() != null) {
            if (ticketType.getPrice().compareTo(BigDecimal.ZERO) <= 0) {
                throw new RuntimeException("价格必须大于0");
            }
            existType.setPrice(ticketType.getPrice());
        }

        // 校验库存
        if (ticketType.getStock() != null) {
            if (ticketType.getStock() < 0) {
                throw new RuntimeException("库存必须大于等于0");
            }
            existType.setStock(ticketType.getStock());
        }

        if (ticketType.getValidStart() != null) {
            existType.setValidStart(ticketType.getValidStart());
        }
        if (ticketType.getValidEnd() != null) {
            existType.setValidEnd(ticketType.getValidEnd());
        }

        // 使用LambdaUpdateWrapper显式更新，避免乐观锁参数问题
        LambdaUpdateWrapper<TicketType> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(TicketType::getId, existType.getId())
                .eq(TicketType::getVersion, originalVersion != null ? originalVersion : 0); // 乐观锁条件
        
        // 只更新有值的字段
        if (ticketType.getScenicId() != null) {
            updateWrapper.set(TicketType::getScenicId, existType.getScenicId());
        }
        if (StringUtils.hasText(ticketType.getTypeName())) {
            updateWrapper.set(TicketType::getTypeName, existType.getTypeName());
        }
        if (ticketType.getPrice() != null) {
            updateWrapper.set(TicketType::getPrice, existType.getPrice());
        }
        if (ticketType.getStock() != null) {
            updateWrapper.set(TicketType::getStock, existType.getStock());
        }
        if (ticketType.getValidStart() != null) {
            updateWrapper.set(TicketType::getValidStart, existType.getValidStart());
        }
        if (ticketType.getValidEnd() != null) {
            updateWrapper.set(TicketType::getValidEnd, existType.getValidEnd());
        }
        
        // 更新version和updateTime
        updateWrapper.set(TicketType::getVersion, (originalVersion != null ? originalVersion : 0) + 1)
                .set(TicketType::getUpdateTime, LocalDateTime.now());
        
        int updateCount = ticketTypeMapper.update(null, updateWrapper);
        if (updateCount == 0) {
            throw new RuntimeException("更新失败，可能数据已被其他用户修改，请刷新后重试");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        TicketType ticketType = getById(id);
        if (ticketType == null) {
            throw new RuntimeException("票种不存在");
        }

        // 校验是否关联订单
        LambdaQueryWrapper<TicketOrder> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(TicketOrder::getTicketTypeId, id);
        Long count = ticketOrderMapper.selectCount(wrapper);
        if (count > 0) {
            throw new RuntimeException("该票种关联订单，禁止删除");
        }

        ticketTypeMapper.deleteById(id);
    }

    @Override
    public TicketType getById(Long id) {
        return ticketTypeMapper.selectById(id);
    }

    @Override
    public Integer getStock(Long id) {
        TicketType ticketType = getById(id);
        return ticketType != null ? ticketType.getStock() : 0;
    }

    @Override
    public PageResult<TicketTypeVO> listWithScenicName(Integer pageNum, Integer pageSize, Long scenicId, String typeName, 
                                                        BigDecimal priceMin, BigDecimal priceMax) {
        // 先查询票种数据
        PageResult<TicketType> ticketResult = list(pageNum, pageSize, scenicId, typeName, priceMin, priceMax);
        
        // 获取所有景区ID
        List<Long> scenicIds = ticketResult.getRecords().stream()
                .map(TicketType::getScenicId)
                .distinct()
                .collect(Collectors.toList());
        
        // 批量查询景区信息
        Map<Long, String> scenicNameMap = scenicIds.stream()
                .collect(Collectors.toMap(
                        id -> id,
                        id -> {
                            ScenicSpot scenic = scenicSpotMapper.selectById(id);
                            return scenic != null ? scenic.getName() : "未知景区";
                        }
                ));
        
        // 转换为VO并填充景区名称
        List<TicketTypeVO> voList = ticketResult.getRecords().stream().map(ticketType -> {
            TicketTypeVO vo = new TicketTypeVO();
            BeanUtils.copyProperties(ticketType, vo);
            vo.setScenicName(scenicNameMap.get(ticketType.getScenicId()));
            return vo;
        }).collect(Collectors.toList());
        
        return new PageResult<>(ticketResult.getTotal(), voList);
    }
}
