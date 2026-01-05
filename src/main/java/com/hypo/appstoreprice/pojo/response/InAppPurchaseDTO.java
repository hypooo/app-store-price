package com.hypo.appstoreprice.pojo.response;

import cn.hutool.core.util.StrUtil;
import com.hypo.appstoreprice.pojo.bean.Money;
import com.hypo.appstoreprice.pojo.enums.AreaEnum;
import lombok.Data;

import java.math.BigDecimal;

/**
 * in-app purchase dto
 *
 * @author hypo
 * @date 2025-09-16
 */
@Data
public class InAppPurchaseDTO {

    /**
     * object
     */
    private String object;

    /**
     * price
     */
    private Money price;

    /**
     * none
     *
     * @return {@link InAppPurchaseDTO }
     */
    public static InAppPurchaseDTO none() {
        InAppPurchaseDTO purchaseDTO = new InAppPurchaseDTO();
        purchaseDTO.setObject(StrUtil.EMPTY);
        purchaseDTO.setPrice(new Money(AreaEnum.USA.getCurrencyCode(), BigDecimal.ZERO));
        return purchaseDTO;
    }

}
