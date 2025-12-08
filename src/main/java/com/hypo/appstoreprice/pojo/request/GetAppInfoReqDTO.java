package com.hypo.appstoreprice.pojo.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * get app info req dto
 *
 * @author hypo
 * @date 2025-09-16
 */
@Data
public class GetAppInfoReqDTO {

    /**
     * app id
     */
    @NotBlank(message = "appId can not be blank")
    private String appId;

}
