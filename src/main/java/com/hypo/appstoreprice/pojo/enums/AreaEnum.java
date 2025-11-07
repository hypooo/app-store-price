package com.hypo.appstoreprice.pojo.enums;

import cn.hutool.core.util.StrUtil;
import com.hypo.appstoreprice.common.BizException;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * area enum
 * https://www.apple.com/choose-country-region/
 * https://www.exchangerate-api.com/docs/supported-currencies
 * https://www.zhihu.com/question/23095211
 *
 * @author hypo
 * @date 2025-09-16
 */
@Getter
@AllArgsConstructor
public enum AreaEnum {

    USA("us", "美国", "$", "USD", StrUtil.COMMA, "In-App Purchases"),

    CHINA("cn", "中国", "¥", "CNY", StrUtil.COMMA, "App内购买"),

    TAIWAN("tw", "台湾", "NT$", "TWD", StrUtil.COMMA, "App內購買"),

    HONGKONG("hk", "香港", "HK$", "HKD", StrUtil.COMMA, "App 內購買"),

    JAPAN("jp", "日本", "¥", "JPY", StrUtil.COMMA, "アプリ内課金"),

    KOREA("kr", "韩国", "₩", "KRW", StrUtil.COMMA, "앱 내 구입"),

    TURKEY("tr", "土耳其", "₺", "TRY", StrUtil.DOT, "In-App Purchases"),

    NIGERIA("ng", "尼日利亚", "₦", "NGN", StrUtil.COMMA, "In-App Purchases"),

    INDIA("in", "印度", "₹", "INR", StrUtil.COMMA, "In-App Purchases"),

    PAKISTAN("pk", "巴基斯坦", "₨", "PKR", StrUtil.COMMA, "In-App Purchases"),

    BRAZIL("br", "巴西", "R$", "BRL", StrUtil.DOT, "Compras dentro do app"),

    ;

    /**
     * code
     */
    private final String code;

    /**
     * name
     */
    private final String name;

    /**
     * currency
     */
    private final String currency;

    /**
     * currency code
     */
    private final String currencyCode;

    /**
     * thousands separator
     */
    private final String thousandsSeparator;

    /**
     * in app purchase str
     */
    private final String InAppPurchaseStr;

    /**
     * get by currency code
     *
     * @param currencyCode currency code
     * @return {@link AreaEnum }
     */
    public static AreaEnum getByCurrencyCode(String currencyCode) {
        for (AreaEnum areaEnum : values()) {
            if (StrUtil.equals(areaEnum.getCurrencyCode(), currencyCode)) {
                return areaEnum;
            }
        }
        throw new BizException("area not found, currencyCode: {}", currencyCode);
    }

}
