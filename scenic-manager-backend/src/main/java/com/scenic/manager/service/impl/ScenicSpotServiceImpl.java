package com.scenic.manager.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.scenic.manager.common.PageResult;
import com.scenic.manager.entity.ScenicSpot;
import com.scenic.manager.entity.TicketType;
import com.scenic.manager.mapper.ScenicSpotMapper;
import com.scenic.manager.mapper.TicketTypeMapper;
import com.scenic.manager.service.ScenicSpotService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;

/**
 * 景区服务实现类
 */
@Service
@RequiredArgsConstructor
public class ScenicSpotServiceImpl implements ScenicSpotService {
    
    private final ScenicSpotMapper scenicSpotMapper;
    private final TicketTypeMapper ticketTypeMapper;

    @Override
    public PageResult<ScenicSpot> list(Integer pageNum, Integer pageSize, String name, String area) {
        Page<ScenicSpot> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<ScenicSpot> wrapper = new LambdaQueryWrapper<>();
        
        if (StringUtils.hasText(name)) {
            wrapper.like(ScenicSpot::getName, name);
        }
        if (StringUtils.hasText(area)) {
            wrapper.like(ScenicSpot::getArea, area);
        }
        
        wrapper.orderByDesc(ScenicSpot::getCreateTime);
        Page<ScenicSpot> result = scenicSpotMapper.selectPage(page, wrapper);
        
        return new PageResult<>(result.getTotal(), result.getRecords());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void add(ScenicSpot scenicSpot) {
        if (!StringUtils.hasText(scenicSpot.getName())) {
            throw new RuntimeException("景区名称不能为空");
        }

        scenicSpot.setCreateTime(LocalDateTime.now());
        scenicSpot.setUpdateTime(LocalDateTime.now());
        scenicSpotMapper.insert(scenicSpot);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(ScenicSpot scenicSpot) {
        ScenicSpot existSpot = getById(scenicSpot.getId());
        if (existSpot == null) {
            throw new RuntimeException("景区不存在");
        }

        if (!StringUtils.hasText(scenicSpot.getName())) {
            throw new RuntimeException("景区名称不能为空");
        }

        existSpot.setName(scenicSpot.getName());
        if (scenicSpot.getIntro() != null) {
            existSpot.setIntro(scenicSpot.getIntro());
        }
        if (scenicSpot.getArea() != null) {
            existSpot.setArea(scenicSpot.getArea());
        }
        existSpot.setUpdateTime(LocalDateTime.now());
        scenicSpotMapper.updateById(existSpot);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        ScenicSpot scenicSpot = getById(id);
        if (scenicSpot == null) {
            throw new RuntimeException("景区不存在");
        }

        // 校验是否关联票种
        LambdaQueryWrapper<TicketType> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(TicketType::getScenicId, id);
        Long count = ticketTypeMapper.selectCount(wrapper);
        if (count > 0) {
            throw new RuntimeException("该景区关联票种，禁止删除");
        }

        scenicSpotMapper.deleteById(id);
    }

    @Override
    public ScenicSpot getById(Long id) {
        return scenicSpotMapper.selectById(id);
    }
}
