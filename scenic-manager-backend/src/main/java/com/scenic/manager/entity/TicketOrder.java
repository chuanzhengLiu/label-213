package com.scenic.manager.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 订单表实体类
 */
@Data
@TableName("ticket_order")
public class TicketOrder {
    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 订单编号（唯一）
     */
    private String orderNo;

    /**
     * 关联景区ID
     */
    private Long scenicId;

    /**
     * 关联票种ID
     */
    private Long ticketTypeId;

    /**
     * 购票人姓名（非空）
     */
    private String userName;

    /**
     * 购票人手机号（非空）
     */
    private String userPhone;

    /**
     * 购票数量（非空，≥1）
     */
    private Integer ticketNum;

    /**
     * 订单总金额（非空）
     */
    private BigDecimal totalAmount;

    /**
     * 订单状态：1-已下单，2-已核销，3-已取消
     */
    private Integer orderStatus;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}
