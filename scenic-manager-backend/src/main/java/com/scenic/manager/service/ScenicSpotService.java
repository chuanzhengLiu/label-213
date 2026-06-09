package com.scenic.manager.service;

import com.scenic.manager.common.PageResult;
import com.scenic.manager.entity.ScenicSpot;

/**
 * 景区服务接口
 */
public interface ScenicSpotService {
    /**
     * 分页查询景区
     * 
     * @param pageNum 页码
     * @param pageSize 每页大小
     * @param name 景区名称（模糊查询）
     * @param area 区域（模糊查询）
     * @return 分页结果
     */
    PageResult<ScenicSpot> list(Integer pageNum, Integer pageSize, String name, String area);

    /**
     * 新增景区
     * 
     * @param scenicSpot 景区信息
     */
    void add(ScenicSpot scenicSpot);

    /**
     * 修改景区
     * 
     * @param scenicSpot 景区信息
     */
    void update(ScenicSpot scenicSpot);

    /**
     * 删除景区
     * 
     * @param id 景区ID
     */
    void delete(Long id);

    /**
     * 根据ID查询景区
     * 
     * @param id 景区ID
     * @return 景区信息
     */
    ScenicSpot getById(Long id);
}
