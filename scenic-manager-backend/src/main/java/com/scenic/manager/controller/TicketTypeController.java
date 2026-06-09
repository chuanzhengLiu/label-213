package com.scenic.manager.controller;

import com.scenic.manager.common.PageResult;
import com.scenic.manager.common.Result;
import com.scenic.manager.entity.TicketType;
import com.scenic.manager.service.TicketTypeService;
import com.scenic.manager.utils.ThreadLocalUtil;
import com.scenic.manager.vo.TicketTypeVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

/**
 * 票种管理控制器
 */
@Tag(name = "票种管理")
@RestController
@RequestMapping("/api/ticket")
@RequiredArgsConstructor
public class TicketTypeController {
    
    private final TicketTypeService ticketTypeService;

    /**
     * 分页查询票种（包含景区名称）
     * GET /api/ticket/list
     */
    @Operation(summary = "分页查询票种")
    @GetMapping("/list")
    public Result<PageResult<TicketTypeVO>> list(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) Long scenicId,
            @RequestParam(required = false) String typeName,
            @RequestParam(required = false) BigDecimal priceMin,
            @RequestParam(required = false) BigDecimal priceMax) {
        PageResult<TicketTypeVO> result = ticketTypeService.listWithScenicName(pageNum, pageSize, scenicId, typeName, priceMin, priceMax);
        return Result.success(result);
    }

    /**
     * 新增票种
     * POST /api/ticket/add
     */
    @Operation(summary = "新增票种")
    @PostMapping("/add")
    public Result<?> add(@RequestBody TicketType ticketType) {
        try {
            ticketTypeService.add(ticketType);
            return Result.success("新增票种成功", null);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 修改票种
     * PUT /api/ticket/update
     */
    @Operation(summary = "修改票种")
    @PutMapping("/update")
    public Result<?> update(@RequestBody TicketType ticketType) {
        try {
            ticketTypeService.update(ticketType);
            return Result.success("修改票种成功", null);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 删除票种
     * DELETE /api/ticket/delete/{id}
     */
    @Operation(summary = "删除票种")
    @DeleteMapping("/delete/{id}")
    public Result<?> delete(@PathVariable Long id) {
        try {
            // 检查权限：普通管理员不能删除
            Integer role = ThreadLocalUtil.getRole();
            if (role != null && role == 2) {
                return Result.forbidden("无操作权限");
            }
            
            ticketTypeService.delete(id);
            return Result.success("删除票种成功", null);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 获取票种库存
     * GET /api/ticket/stock/{id}
     */
    @Operation(summary = "获取票种库存")
    @GetMapping("/stock/{id}")
    public Result<Integer> getStock(@PathVariable Long id) {
        Integer stock = ticketTypeService.getStock(id);
        return Result.success(stock);
    }
}
