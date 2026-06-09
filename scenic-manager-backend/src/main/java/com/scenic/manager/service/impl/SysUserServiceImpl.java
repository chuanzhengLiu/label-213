package com.scenic.manager.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.scenic.manager.common.PageResult;
import com.scenic.manager.entity.SysUser;
import com.scenic.manager.mapper.SysUserMapper;
import com.scenic.manager.service.SysUserService;
import com.scenic.manager.utils.JwtUtil;
import com.scenic.manager.utils.ThreadLocalUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 用户服务实现类
 */
@Service
@RequiredArgsConstructor
public class SysUserServiceImpl implements SysUserService {
    
    private final SysUserMapper userMapper;
    private final BCryptPasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final RedisTemplate<String, Object> redisTemplate;

    @Override
    public Object login(String username, String password) {
        // 查询用户
        SysUser user = getByUsername(username);
        if (user == null) {
            throw new RuntimeException("用户名或密码错误");
        }

        // 验证密码
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new RuntimeException("用户名或密码错误");
        }

        // 生成JWT token
        String token = jwtUtil.generateToken(user.getId(), user.getUsername(), user.getRole());

        // 将token存储到Redis（2小时过期）
        String redisKey = "token:" + user.getId();
        redisTemplate.opsForValue().set(redisKey, token, 2, TimeUnit.HOURS);

        // 返回结果
        Map<String, Object> result = new HashMap<>();
        result.put("token", token);
        
        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("id", user.getId());
        userInfo.put("username", user.getUsername());
        userInfo.put("role", user.getRole());
        result.put("userInfo", userInfo);

        return result;
    }

    @Override
    public PageResult<SysUser> list(Integer pageNum, Integer pageSize, String username, Integer role) {
        Page<SysUser> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        
        if (StringUtils.hasText(username)) {
            wrapper.like(SysUser::getUsername, username);
        }
        if (role != null) {
            wrapper.eq(SysUser::getRole, role);
        }
        
        wrapper.orderByDesc(SysUser::getCreateTime);
        Page<SysUser> result = userMapper.selectPage(page, wrapper);
        
        return new PageResult<>(result.getTotal(), result.getRecords());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void add(SysUser user) {
        // 校验用户名唯一性
        SysUser existUser = getByUsername(user.getUsername());
        if (existUser != null) {
            throw new RuntimeException("用户名已存在");
        }

        // 校验角色
        if (user.getRole() == null || (user.getRole() != 1 && user.getRole() != 2)) {
            throw new RuntimeException("角色必须为1（超级管理员）或2（普通管理员）");
        }

        // 密码加密
        if (!StringUtils.hasText(user.getPassword())) {
            user.setPassword("123456"); // 默认密码
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        user.setCreateTime(LocalDateTime.now());
        user.setUpdateTime(LocalDateTime.now());
        userMapper.insert(user);
    }

    @PostConstruct
    public void init() {
        System.err.println( passwordEncoder.encode("123456")
);
    }
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(SysUser user) {
        SysUser existUser = getById(user.getId());
        if (existUser == null) {
            throw new RuntimeException("用户不存在");
        }

        // 禁止修改超级管理员自身角色
        Long currentUserId = ThreadLocalUtil.getUserId();
        if (currentUserId != null && currentUserId.equals(user.getId()) && existUser.getRole() == 1) {
            if (user.getRole() != null && !user.getRole().equals(1)) {
                throw new RuntimeException("禁止修改超级管理员自身角色");
            }
        }

        // 校验用户名唯一性（如果修改了用户名）
        if (StringUtils.hasText(user.getUsername()) && !user.getUsername().equals(existUser.getUsername())) {
            SysUser duplicateUser = getByUsername(user.getUsername());
            if (duplicateUser != null) {
                throw new RuntimeException("用户名已存在");
            }
            existUser.setUsername(user.getUsername());
        }

        // 校验角色
        if (user.getRole() != null) {
            if (user.getRole() != 1 && user.getRole() != 2) {
                throw new RuntimeException("角色必须为1（超级管理员）或2（普通管理员）");
            }
            existUser.setRole(user.getRole());
        }

        existUser.setUpdateTime(LocalDateTime.now());
        userMapper.updateById(existUser);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        SysUser user = getById(id);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }

        // 禁止删除超级管理员
        if (user.getRole() == 1) {
            throw new RuntimeException("禁止删除超级管理员账号");
        }

        userMapper.deleteById(id);
    }

    @Override
    public SysUser getById(Long id) {
        return userMapper.selectById(id);
    }

    @Override
    public SysUser getByUsername(String username) {
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUser::getUsername, username);
        return userMapper.selectOne(wrapper);
    }
}
