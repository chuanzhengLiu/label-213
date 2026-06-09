package com.scenic.manager.controller;

import com.scenic.manager.common.PageResult;
import com.scenic.manager.common.Result;
import com.scenic.manager.entity.ScenicSpot;
import com.scenic.manager.service.ScenicSpotService;
import com.scenic.manager.utils.ThreadLocalUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 景区管理控制器
 */
@Tag(name = "景区管理")
@RestController
@RequestMapping("/api/scenic")
@RequiredArgsConstructor
public class ScenicSpotController {
    
    private final ScenicSpotService scenicSpotService;

    /**
     * 分页查询景区
     * GET /api/scenic/list
     */
    @Operation(summary = "分页查询景区")
    @GetMapping("/list")
    public Result<PageResult<ScenicSpot>> list(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String area) {
        PageResult<ScenicSpot> result = scenicSpotService.list(pageNum, pageSize, name, area);
        return Result.success(result);
    }

    /**
     * 新增景区
     * POST /api/scenic/add
     */
    @Operation(summary = "新增景区")
    @PostMapping("/add")
    public Result<?> add(@RequestBody ScenicSpot scenicSpot) {
        try {
            scenicSpotService.add(scenicSpot);
            return Result.success("新增景区成功", null);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 修改景区
     * PUT /api/scenic/update
     */
    @Operation(summary = "修改景区")
    @PutMapping("/update")
    public Result<?> update(@RequestBody ScenicSpot scenicSpot) {
        try {
            scenicSpotService.update(scenicSpot);
            return Result.success("修改景区成功", null);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 删除景区
     * DELETE /api/scenic/delete/{id}
     */
    @Operation(summary = "删除景区")
    @DeleteMapping("/delete/{id}")
    public Result<?> delete(@PathVariable Long id) {
        try {
            // 检查权限：普通管理员不能删除
            Integer role = ThreadLocalUtil.getRole();
            if (role != null && role == 2) {
                return Result.forbidden("无操作权限");
            }
            
            scenicSpotService.delete(id);
            return Result.success("删除景区成功", null);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
}
