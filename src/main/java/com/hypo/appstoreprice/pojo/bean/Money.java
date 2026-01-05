package com.hypo.appstoreprice.pojo.bean;

import com.hypo.appstoreprice.common.ExchangeRateUtil;
import com.hypo.appstoreprice.pojo.enums.AreaEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * money
 *
 * @author hypo
 * @date 2025-09-16
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Money {

    /**
     * area
     */
    private String area;

    /**
     * area name
     */
    private String areaName;

    /**
     * currency
     */
    private String currency;

    /**
     * currency code
     */
    private String currencyCode;

    /**
     * locale
     */
    private String locale;

    /**
     * price
     */
    private BigDecimal price;

    /**
     * cny price
     */
    private BigDecimal cnyPrice;

    /**
     * money
     *
     * @param currencyCode currency code
     * @param price        price
     */
    public Money(String currencyCode, BigDecimal price) {
        AreaEnum areaEnum = AreaEnum.getByCurrencyCode(currencyCode);
        this.area = areaEnum.getCode();
        this.areaName = areaEnum.getName();
        this.currency = areaEnum.getCurrency();
        this.currencyCode = currencyCode;
        this.locale = areaEnum.getLocale();
        this.price = price;
        this.cnyPrice = ExchangeRateUtil.convertToCny(price, currencyCode);
    }

}
