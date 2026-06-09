package com.scenic.manager.dto;

import lombok.Data;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

/**
 * 创建订单DTO
 */
@Data
public class OrderCreateDTO {
    @NotNull(message = "景区ID不能为空")
    private Long scenicId;

    @NotNull(message = "票种ID不能为空")
    private Long ticketTypeId;

    @NotBlank(message = "购票人姓名不能为空")
    private String userName;

    @NotBlank(message = "购票人手机号不能为空")
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    private String userPhone;

    @NotNull(message = "购票数量不能为空")
    @Min(value = 1, message = "购票数量至少为1")
    private Integer ticketNum;
}
