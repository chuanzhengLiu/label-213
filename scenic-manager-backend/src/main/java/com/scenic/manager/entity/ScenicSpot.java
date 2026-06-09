package com.scenic.manager.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 景区表实体类
 */
@Data
@TableName("scenic_spot")
public class ScenicSpot {
    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 景区名称（非空）
     */
    private String name;

    /**
     * 景区简介
     */
    private String intro;

    /**
     * 所在区域
     */
    private String area;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}
