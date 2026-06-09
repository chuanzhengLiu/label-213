package com.scenic.manager.controller;

import com.scenic.manager.common.PageResult;
import com.scenic.manager.common.Result;
import com.scenic.manager.dto.LoginDTO;
import com.scenic.manager.dto.UserAddDTO;
import com.scenic.manager.entity.SysUser;
import com.scenic.manager.service.SysUserService;
import com.scenic.manager.utils.ThreadLocalUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 用户管理控制器
 */
@Tag(name = "用户管理")
@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class SysUserController {
    
    private final SysUserService userService;
    private final BCryptPasswordEncoder passwordEncoder;

    /**
     * 用户登录
     * POST /api/user/login
     */
    @Operation(summary = "用户登录")
    @PostMapping("/login")
    public Result<?> login(@Validated @RequestBody LoginDTO dto) {
        try {
            Object result = userService.login(dto.getUsername(), dto.getPassword());
            return Result.success("登录成功", result);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 获取当前用户信息
     * GET /api/user/current
     */
    @Operation(summary = "获取当前用户信息")
    @GetMapping("/current")
    public Result<?> getCurrentUser() {
        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("id", ThreadLocalUtil.getUserId());
        userInfo.put("username", ThreadLocalUtil.getUsername());
        userInfo.put("role", ThreadLocalUtil.getRole());
        return Result.success(userInfo);
    }

    /**
     * 分页查询用户
     * GET /api/user/list
     */
    @Operation(summary = "分页查询用户")
    @GetMapping("/list")
    public Result<PageResult<SysUser>> list(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String username,
            @RequestParam(required = false) Integer role) {
        PageResult<SysUser> result = userService.list(pageNum, pageSize, username, role);
        return Result.success(result);
    }

    /**
     * 新增用户
     * POST /api/user/add
     */
    @Operation(summary = "新增用户")
    @PostMapping("/add")
    public Result<?> add(@Validated @RequestBody UserAddDTO dto) {
        try {
            SysUser user = new SysUser();
            user.setUsername(dto.getUsername());
            user.setPassword(dto.getPassword());
            user.setRole(dto.getRole());
            userService.add(user);
            return Result.success("新增用户成功", null);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 修改用户
     * PUT /api/user/update
     */
    @Operation(summary = "修改用户")
    @PutMapping("/update")
    public Result<?> update(@RequestBody SysUser user) {
        try {
            userService.update(user);
            return Result.success("修改用户成功", null);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 删除用户
     * DELETE /api/user/delete/{id}
     */
    @Operation(summary = "删除用户")
    @DeleteMapping("/delete/{id}")
    public Result<?> delete(@PathVariable Long id) {
        try {
            // 检查权限：普通管理员不能删除
            Integer role = ThreadLocalUtil.getRole();
            if (role != null && role == 2) {
                return Result.forbidden("无操作权限");
            }
            
            userService.delete(id);
            return Result.success("删除用户成功", null);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
}
