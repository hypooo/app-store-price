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

    USA("us", "美国", "$", "USD", StrUtil.COMMA, "In-App Purchases", "en-US"),

    CHINA("cn", "中国", "¥", "CNY", StrUtil.COMMA, "App内购买", "zh-CN"),

    TAIWAN("tw", "台湾", "NT$", "TWD", StrUtil.COMMA, "App內購買", "zh-TW"),

    HONGKONG("hk", "香港", "HK$", "HKD", StrUtil.COMMA, "App 內購買", "zh-HK"),

    JAPAN("jp", "日本", "¥", "JPY", StrUtil.COMMA, "アプリ内課金", "ja-JP"),

    KOREA("kr", "韩国", "₩", "KRW", StrUtil.COMMA, "앱 내 구입", "ko-KR"),

    TURKEY("tr", "土耳其", "₺", "TRY", StrUtil.DOT, "In-App Purchases", "tr-TR"),

    NIGERIA("ng", "尼日利亚", "₦", "NGN", StrUtil.COMMA, "In-App Purchases", "en-NG"),

    INDIA("in", "印度", "₹", "INR", StrUtil.COMMA, "In-App Purchases", "en-IN"),

    PAKISTAN("pk", "巴基斯坦", "₨", "PKR", StrUtil.COMMA, "In-App Purchases", "en-PK"),

    BRAZIL("br", "巴西", "R$", "BRL", StrUtil.DOT, "Compras dentro do app", "pt-BR"),

    EGYPT("eg", "埃及", "E£", "EGP", StrUtil.COMMA, "In-App Purchases", "ar-EG-u-nu-latn"),

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
     * locale
     */
    private final String locale;

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
