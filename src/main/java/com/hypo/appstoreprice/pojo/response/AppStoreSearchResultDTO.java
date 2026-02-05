package com.hypo.appstoreprice.pojo.response;

import lombok.Data;

/**
 * app store search result dto
 *
 * @author hypo
 * @date 2025-10-20
 */
@Data
public class AppStoreSearchResultDTO {

    /**
     * adamId
     */
    private String adamId;

    /**
     * title
     */
    private String title;

    /**
     * subtitle
     */
    private String subtitle;

    /**
     * icon
     */
    private Icon icon;

    /**
     * icon
     *
     * @author hypo
     * @date 2026-02-05
     */
    @Data
    public static class Icon {

        /**
         * template
         */
        private String template;

        public String getTemplate() {
            return template.replace("{w}x{h}{c}.{f}", "512x512bb.jpg");
        }

    }

}
