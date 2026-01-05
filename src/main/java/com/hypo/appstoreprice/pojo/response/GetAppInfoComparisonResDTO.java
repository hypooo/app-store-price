package com.hypo.appstoreprice.pojo.response;

import com.hypo.appstoreprice.pojo.bean.Money;
import lombok.Data;

import java.util.List;

/**
 * get app info comparison res dto
 *
 * @author hypo
 * @date 2026-01-05
 */
@Data
public class GetAppInfoComparisonResDTO {

    /**
     * object
     */
    private String object;

    /**
     * price list
     */
    private List<Money> priceList;

}
