package com.hypo.appstoreprice.pojo.response;

import lombok.Data;

import java.math.BigDecimal;

/**
 * app store search result dto
 *
 * @author hypo
 * @date 2025-10-20
 */
@Data
public class AppStoreSearchResultDTO {

    /**
     * track id
     */
    private String trackId;

    /**
     * track name
     */
    private String trackName;

    /**
     * artwork url512
     */
    private String artworkUrl512;

    /**
     * description
     */
    private String description;

    /**
     * average user rating
     */
    private BigDecimal averageUserRating;

}
