package com.hypo.appstoreprice.common;

import cn.hutool.cache.Cache;
import cn.hutool.cache.impl.TimedCache;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.hypo.appstoreprice.pojo.enums.AreaEnum;
import lombok.experimental.UtilityClass;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * exchange rate util
 *
 * @author hypo
 * @date 2025-09-16
 */
@UtilityClass
public class ExchangeRateUtil {

    private final static Cache<String, Map<String, BigDecimal>> RATE_CACHE = new TimedCache<>(Duration.ofDays(1L).toMillis(), new ConcurrentHashMap<>());

    /**
     * get exchange rate map
     *
     * @param currencyCode currency code
     * @return {@link Map }<{@link String }, {@link BigDecimal }>
     */
    private Map<String, BigDecimal> getExchangeRateMap(String currencyCode) {
        if (Objects.nonNull(RATE_CACHE.get(currencyCode))) {
            return RATE_CACHE.get(currencyCode);
        }
        synchronized (ExchangeRateUtil.class) {
            if (Objects.nonNull(RATE_CACHE.get(currencyCode))) {
                return RATE_CACHE.get(currencyCode);
            }
            JSONObject result = JSON.parseObject(HttpUtil.get(StrUtil.format("https://open.er-api.com/v6/latest/{}", currencyCode)));
            if (Objects.isNull(result)) {
                throw new BizException("get exchange rate failed");
            }
            Map<String, BigDecimal> resultMap = new HashMap<>();
            result.getJSONObject("rates").forEach((key, value) -> resultMap.put(key, new BigDecimal(Convert.toStr(value))));
            RATE_CACHE.put(currencyCode, resultMap);
            return resultMap;
        }
    }

    /**
     * convert to cny
     *
     * @param amount       amount
     * @param currencyCode currency code
     * @return {@link BigDecimal }
     */
    public BigDecimal convertToCny(BigDecimal amount, String currencyCode) {
        Map<String, BigDecimal> exchangeRateMap = getExchangeRateMap(AreaEnum.CHINA.getCurrencyCode());
        return NumberUtil.div(amount, exchangeRateMap.get(currencyCode), 2, RoundingMode.HALF_UP);
    }

}
