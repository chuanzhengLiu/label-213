package com.scenic.manager.vo;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 票种视图对象（包含景区名称）
 */
@Data
public class TicketTypeVO {
    /**
     * 主键ID
     */
    private Long id;

    /**
     * 关联景区ID
     */
    private Long scenicId;

    /**
     * 景区名称
     */
    private String scenicName;

    /**
     * 票种名称
     */
    private String typeName;

    /**
     * 价格
     */
    private BigDecimal price;

    /**
     * 库存
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
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}
