package com.hypo.appstoreprice.pojo.response;

import com.hypo.appstoreprice.pojo.bean.Money;
import lombok.Data;

import java.util.List;

/**
 * get app info res dto
 *
 * @author hypo
 * @date 2025-09-16
 */
@Data
public class GetAppInfoResDTO {

    /**
     * app id
     */
    private String appId;

    /**
     * area
     */
    private String area;

    /**
     * area name
     */
    private String areaName;

    /**
     * name
     */
    private String name;

    /**
     * sub title
     */
    private String subTitle;

    /**
     * developer
     */
    private String developer;

    /**
     * app store url
     */
    private String appStoreUrl;

    /**
     * price
     */
    private Money price;

    /**
     * in-app purchase list
     */
    private List<InAppPurchaseDTO> inAppPurchaseList;

}
