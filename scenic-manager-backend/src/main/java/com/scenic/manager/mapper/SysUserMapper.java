package com.scenic.manager.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.scenic.manager.entity.SysUser;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户Mapper接口
 */
@Mapper
public interface SysUserMapper extends BaseMapper<SysUser> {
}
