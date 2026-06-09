package com.scenic.manager.common;

import lombok.Data;
import java.util.List;

/**
 * 分页结果封装
 * 
 * @param <T> 数据类型
 */
@Data
public class PageResult<T> {
    /**
     * 总记录数
     */
    private Long total;
    
    /**
     * 数据列表
     */
    private List<T> records;

    public PageResult() {
    }

    public PageResult(Long total, List<T> records) {
        this.total = total;
        this.records = records;
    }
}
