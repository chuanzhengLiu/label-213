package com.scenic.manager.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.Version;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 票种表实体类
 */
@Data
@TableName("ticket_type")
public class TicketType {
    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 关联景区ID
     */
    private Long scenicId;

    /**
     * 票种名称（非空）
     */
    private String typeName;

    /**
     * 价格（非空，正数）
     */
    private BigDecimal price;

    /**
     * 库存（非空，≥0）
     */
    private Integer stock;

    /**
     * 有效期开始时间
     */
    private LocalDate validStart;

    /**
     * 有效期结束时间
     */
    private LocalDate validEnd;

    /**
     * 乐观锁版本号
     */
    @Version
    private Integer version;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}
