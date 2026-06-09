package com.scenic.manager.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.scenic.manager.entity.TicketType;
import org.apache.ibatis.annotations.Mapper;

/**
 * 票种Mapper接口
 */
@Mapper
public interface TicketTypeMapper extends BaseMapper<TicketType> {
}
