package com.scenic.manager.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.scenic.manager.common.PageResult;
import com.scenic.manager.entity.SysUser;

/**
 * 用户服务接口
 */
public interface SysUserService {
    /**
     * 用户登录
     * 
     * @param username 用户名
     * @param password 密码
     * @return token和用户信息
     */
    Object login(String username, String password);

    /**
     * 分页查询用户
     * 
     * @param pageNum 页码
     * @param pageSize 每页大小
     * @param username 用户名（模糊查询）
     * @param role 角色
     * @return 分页结果
     */
    PageResult<SysUser> list(Integer pageNum, Integer pageSize, String username, Integer role);

    /**
     * 新增用户
     * 
     * @param user 用户信息
     */
    void add(SysUser user);

    /**
     * 修改用户
     * 
     * @param user 用户信息
     */
    void update(SysUser user);

    /**
     * 删除用户
     * 
     * @param id 用户ID
     */
    void delete(Long id);

    /**
     * 根据ID查询用户
     * 
     * @param id 用户ID
     * @return 用户信息
     */
    SysUser getById(Long id);

    /**
     * 根据用户名查询用户
     * 
     * @param username 用户名
     * @return 用户信息
     */
    SysUser getByUsername(String username);
}
