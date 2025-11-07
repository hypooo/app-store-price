package com.hypo.appstoreprice.controller;

import com.hypo.appstoreprice.pojo.request.GetAppInfoReqDTO;
import com.hypo.appstoreprice.pojo.request.GetAppListReqDTO;
import com.hypo.appstoreprice.pojo.response.AreaResDTO;
import com.hypo.appstoreprice.pojo.response.GetAppInfoResDTO;
import com.hypo.appstoreprice.pojo.response.GetAppListResDTO;
import com.hypo.appstoreprice.service.AppService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
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
     * get popular search word list
     *
     * @return {@link List }<{@link String }>
     */
    @PostMapping("getPopularSearchWordList")
    public List<String> getPopularSearchWordList() {
        return appService.getPopularSearchWordList();
    }

    /**
     * get app list
     *
     * @param reqDTO req dto
     * @return {@link List }<{@link GetAppListResDTO }>
     */
    @PostMapping("getAppList")
    public List<GetAppListResDTO> getAppList(@RequestBody @Validated GetAppListReqDTO reqDTO) {
        return appService.getAppList(reqDTO);
    }

    /**
     * get app info
     *
     * @return {@link GetAppInfoResDTO }
     */
    @PostMapping("getAppInfo")
    public List<GetAppInfoResDTO> getAppInfo(@RequestBody @Validated GetAppInfoReqDTO reqDTO) {
        return appService.getAppInfo(reqDTO.getAppId());
    }

}
