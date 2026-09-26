package com.hypo.appstoreprice.pojo.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

/**
 * get top app list req dto
 *
 * @author hypo
 * @date 2026-09-26
 */
@Data
public class GetTopAppListReqDTO {

    /**
     * area code
     */
    @NotBlank(message = "areaCode can not be blank")
    private String areaCode;

    /**
     * 榜单类型：top-free 免费榜，top-paid 付费榜
     */
    @NotBlank(message = "chartType can not be blank")
    @Pattern(regexp = "top-free|top-paid", message = "chartType must be top-free or top-paid")
    private String chartType;

}
