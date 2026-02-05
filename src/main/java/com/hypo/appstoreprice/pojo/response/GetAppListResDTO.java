package com.hypo.appstoreprice.pojo.response;

import lombok.Data;

/**
 * get app list res dto
 *
 * @author hypo
 * @date 2025-09-17
 */
@Data
public class GetAppListResDTO {

    /**
     * app id
     */
    private String appId;

    /**
     * app name
     */
    private String appName;

    /**
     * app image
     */
    private String appImage;

    /**
     * app desc
     */
    private String appDesc;

}
