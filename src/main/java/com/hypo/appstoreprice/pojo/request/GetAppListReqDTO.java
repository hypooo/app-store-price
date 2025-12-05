package com.hypo.appstoreprice.pojo.request;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

/**
 * get app list req dto
 *
 * @author hypo
 * @date 2025-09-17
 */
@Data
public class GetAppListReqDTO {

    /**
     * area code
     */
    @NotBlank(message = "areaCode can not be blank")
    private String areaCode;

    /**
     * app name
     */
    @NotBlank(message = "appName can not be blank")
    @Size(max = 20, message = "appName length must be less than or equal to 20")
    private String appName;

}
