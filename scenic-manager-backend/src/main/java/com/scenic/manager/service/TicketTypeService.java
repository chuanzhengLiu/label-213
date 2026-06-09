package com.scenic.manager.service;

import com.scenic.manager.common.PageResult;
import com.scenic.manager.entity.TicketType;
import com.scenic.manager.vo.TicketTypeVO;

import java.math.BigDecimal;

/**
 * 票种服务接口
 */
public interface TicketTypeService {
    /**
     * 分页查询票种
     * 
     * @param pageNum 页码
     * @param pageSize 每页大小
     * @param scenicId 景区ID
     * @param typeName 票种名称（模糊查询）
     * @param priceMin 最低价格
     * @param priceMax 最高价格
     * @return 分页结果
     */
    PageResult<TicketType> list(Integer pageNum, Integer pageSize, Long scenicId, String typeName, 
                                BigDecimal priceMin, BigDecimal priceMax);

    /**
     * 分页查询票种（包含景区名称）
     * 
     * @param pageNum 页码
     * @param pageSize 每页大小
     * @param scenicId 景区ID（可选，null表示查询全部）
     * @param typeName 票种名称（模糊查询）
     * @param priceMin 最低价格
     * @param priceMax 最高价格
     * @return 分页结果（包含景区名称）
     */
    PageResult<TicketTypeVO> listWithScenicName(Integer pageNum, Integer pageSize, Long scenicId, String typeName, 
                                                BigDecimal priceMin, BigDecimal priceMax);

    /**
     * 新增票种
     * 
     * @param ticketType 票种信息
     */
    void add(TicketType ticketType);

    /**
     * 修改票种
     * 
     * @param ticketType 票种信息
     */
    void update(TicketType ticketType);

    /**
     * 删除票种
     * 
     * @param id 票种ID
     */
    void delete(Long id);

    /**
     * 根据ID查询票种
     * 
     * @param id 票种ID
     * @return 票种信息
     */
    TicketType getById(Long id);

    /**
     * 获取票种库存
     * 
     * @param id 票种ID
     * @return 库存数量
     */
    Integer getStock(Long id);
}
