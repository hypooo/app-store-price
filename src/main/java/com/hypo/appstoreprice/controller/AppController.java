package com.hypo.appstoreprice.controller;

import com.hypo.appstoreprice.pojo.request.GetAppInfoReqDTO;
import com.hypo.appstoreprice.pojo.request.GetAppListReqDTO;
import com.hypo.appstoreprice.pojo.request.GetPopularAppListReqDTO;
import com.hypo.appstoreprice.pojo.response.AreaResDTO;
import com.hypo.appstoreprice.pojo.response.GetAppInfoComparisonResDTO;
import com.hypo.appstoreprice.pojo.response.GetAppInfoResDTO;
import com.hypo.appstoreprice.pojo.response.GetAppListResDTO;
import com.hypo.appstoreprice.service.AppPopularityService;
import com.hypo.appstoreprice.service.AppService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * app controller
 *
 * @author hypo
 * @date 2025-09-16
 */
@RestController
@RequestMapping("app")
@RequiredArgsConstructor
public class AppController {

    private final AppService appService;

    private final AppPopularityService appPopularityService;

    /**
     * get area list
     *
     * @return {@link List }<{@link AreaResDTO }>
     */
    @PostMapping("getAreaList")
    public List<AreaResDTO> getAreaList() {
        return appService.getAreaList();
    }

    /**
     * get popular app list
     *
     * @return {@link List }<{@link GetAppListResDTO }>
     */
    @PostMapping("getPopularAppList")
    public List<GetAppListResDTO> getPopularAppList(@RequestBody @Validated GetPopularAppListReqDTO reqDTO) {
        return appPopularityService.getPopularAppList(reqDTO.getAreaCode());
    }

    /**
     * get app list
     *
     * @param reqDTO req dto
     * @return {@link List }<{@link GetAppListResDTO }>
     */
    @PostMapping("getAppList")
    public List<GetAppListResDTO> getAppList(@RequestBody @Validated GetAppListReqDTO reqDTO) {
        List<GetAppListResDTO> appList = appService.getAppList(reqDTO);
        appPopularityService.rememberSearchResults(appList);
        return appList;
    }

    /**
     * get app info
     *
     * @return {@link GetAppInfoResDTO }
     */
    @PostMapping("getAppInfo")
    public List<GetAppInfoResDTO> getAppInfo(@RequestBody @Validated GetAppInfoReqDTO reqDTO,
                                           @RequestHeader(value = "X-Fingerprint", required = false) String fingerprint) {
        List<GetAppInfoResDTO> appInfo = appService.getAppInfo(reqDTO.getAppId());
        if (fingerprint != null && !fingerprint.isBlank() && !appInfo.isEmpty()) {
            appPopularityService.recordAppClick(reqDTO.getAppId(), fingerprint, appInfo);
        }
        return appInfo;
    }

    /**
     * get app info comparison
     *
     * @param reqDTO req dto
     * @return {@link List }<{@link GetAppInfoComparisonResDTO }>
     */
    @PostMapping("getAppInfoComparison")
    public List<GetAppInfoComparisonResDTO> getAppInfoComparison(@RequestBody @Validated GetAppInfoReqDTO reqDTO) {
        return appService.getAppInfoComparison(reqDTO.getAppId());
    }

}
