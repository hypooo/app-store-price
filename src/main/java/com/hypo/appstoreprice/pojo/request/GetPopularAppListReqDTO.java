package com.hypo.appstoreprice.pojo.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

/**
 * get popular app list req dto
 *
 * @author hypo
 * @date 2026-09-30
 */
@Data
public class GetPopularAppListReqDTO {

    /**
     * 排行文案的展示地区；不传时默认使用美国。
     */
    @NotNull(message = "areaCode can not be null")
    @Pattern(regexp = "us|cn|tw|hk|jp|kr|ph|tr|ng|in|pk|br|eg", message = "areaCode must be a supported area code")
    private String areaCode = "us";

}
