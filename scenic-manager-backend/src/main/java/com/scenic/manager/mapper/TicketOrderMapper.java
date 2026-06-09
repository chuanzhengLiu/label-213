package com.scenic.manager.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.scenic.manager.entity.TicketOrder;
import org.apache.ibatis.annotations.Mapper;

/**
 * 订单Mapper接口
 */
@Mapper
public interface TicketOrderMapper extends BaseMapper<TicketOrder> {
}
