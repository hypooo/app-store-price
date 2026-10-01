package com.hypo.appstoreprice.pojo.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
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

    /**
     * 应用选择来源；搜索结果和热门排行点击均参与计数
     */
    @Pattern(regexp = "search|popular", message = "source must be search or popular")
    private String source;

}
