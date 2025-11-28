package com.hypo.appstoreprice.service;

import cn.hutool.cache.Cache;
import cn.hutool.cache.impl.TimedCache;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.mutable.Mutable;
import cn.hutool.core.lang.mutable.MutableObj;
import cn.hutool.core.util.ReUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.dtflys.forest.Forest;
import com.dtflys.forest.http.ForestResponse;
import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;
import com.hypo.appstoreprice.common.BizException;
import com.hypo.appstoreprice.pojo.bean.Money;
import com.hypo.appstoreprice.pojo.enums.AreaEnum;
import com.hypo.appstoreprice.pojo.request.GetAppListReqDTO;
import com.hypo.appstoreprice.pojo.response.*;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * app service
 *
 * @author hypo
 * @date 2025-09-16
 */
@Slf4j
@Service
public class AppService {

    /**
     * app 搜索列表缓存
     */
    private static final Cache<String, List<GetAppListResDTO>> APP_LIST_CACHE = new TimedCache<>(Duration.ofDays(1L).toMillis(), new ConcurrentHashMap<>());

    /**
     * app 信息缓存
     */
    private static final Cache<String, List<GetAppInfoResDTO>> APP_INFO_CACHE = new TimedCache<>(Duration.ofDays(1L).toMillis(), new ConcurrentHashMap<>());

    /**
     * lock pool
     */
    private static final ConcurrentHashMap<String, Object> LOCK_POOL = new ConcurrentHashMap<>();

    /**
     * 热门搜索词排行
     */
    private static final Multiset<String> POPULAR_SEARCH_WORD = HashMultiset.create();

    /**
     * get popular search word list
     *
     * @return {@link List }<{@link String }>
     */
    public List<String> getPopularSearchWordList() {
        return POPULAR_SEARCH_WORD.entrySet()
            .stream()
            .sorted(Comparator.comparingInt(Multiset.Entry<String>::getCount).reversed())
            .limit(10)
            .map(Multiset.Entry::getElement)
            .collect(Collectors.toList());
    }

    /**
     * get app list
     *
     * @param reqDTO req dto
     * @return {@link List }<{@link GetAppListResDTO }>
     */
    public List<GetAppListResDTO> getAppList(GetAppListReqDTO reqDTO) {
        // 记录搜索次数
        POPULAR_SEARCH_WORD.add(reqDTO.getAppName());

        // 无锁检查缓存
        String cacheKey = StrUtil.format("{}-{}", reqDTO.getAreaCode(), reqDTO.getAppName());
        List<GetAppListResDTO> appListCache = APP_LIST_CACHE.get(cacheKey);
        if (CollUtil.isNotEmpty(appListCache)) {
            return appListCache;
        }

        // 获取锁对象（细粒度锁，按 appId 分段）
        Object lock = LOCK_POOL.computeIfAbsent(StrUtil.format("getAppList-{}-{}", reqDTO.getAreaCode(), reqDTO.getAppName()), k -> new Object());

        synchronized (lock) {
            // 锁内再次检查缓存（双重检查）
            appListCache = APP_LIST_CACHE.get(cacheKey);
            if (CollUtil.isNotEmpty(appListCache)) {
                return appListCache;
            }

            String searchUrl = StrUtil.format("https://itunes.apple.com/search?term={}&country={}&entity=software", StrUtil.trim(reqDTO.getAppName()), reqDTO.getAreaCode());
            ForestResponse<?> response = Forest.get(searchUrl).execute(ForestResponse.class);
            if (response.getStatusCode() != HttpStatus.OK.value()) {
                String errorMessage = StrUtil.format("search failed, areaCode: {}, appName: {}", reqDTO.getAreaCode(), reqDTO.getAppName());
                log.error(errorMessage);
                throw new BizException(errorMessage);
            }

            JSONObject data = JSON.parseObject(response.readAsString());
            List<AppStoreSearchResultDTO> searchResultList = data.getList("results", AppStoreSearchResultDTO.class);
            List<GetAppListResDTO> resultList = new ArrayList<>(searchResultList.stream().map(item -> {
                GetAppListResDTO dto = new GetAppListResDTO();
                dto.setAppId(item.getTrackId());
                dto.setAppName(item.getTrackName());
                dto.setAppImage(item.getArtworkUrl512());
                dto.setAppDesc(item.getDescription());
                dto.setRating(item.getAverageUserRating());
                return dto;
            }).collect(Collectors.toMap(
                GetAppListResDTO::getAppId,
                Function.identity(),
                (existingValue, newValue) -> existingValue,
                LinkedHashMap::new
            )).values());
            // 缓存结果
            APP_LIST_CACHE.put(cacheKey, resultList);
        }
        return APP_LIST_CACHE.get(cacheKey);
    }

    /**
     * get app info
     *
     * @param appId app id
     * @return {@link GetAppInfoResDTO }
     */
    public List<GetAppInfoResDTO> getAppInfo(String appId) {
        // 无锁检查缓存
        List<GetAppInfoResDTO> appInfoListCache = APP_INFO_CACHE.get(appId);
        if (CollUtil.isNotEmpty(appInfoListCache)) {
            return appInfoListCache;
        }

        // 获取锁对象（细粒度锁，按 appId 分段）
        Object lock = LOCK_POOL.computeIfAbsent(StrUtil.format("getAppInfo-{}", appId), k -> new Object());

        synchronized (lock) {
            // 锁内再次检查缓存（双重检查）
            appInfoListCache = APP_INFO_CACHE.get(appId);
            if (CollUtil.isNotEmpty(appInfoListCache)) {
                return appInfoListCache;
            }
            Mutable<List<GetAppInfoResDTO>> resultList = new MutableObj<>(new CopyOnWriteArrayList<>());
            Arrays.stream(AreaEnum.values()).parallel().forEach(areaEnum -> {
                String appStoreUrl = StrUtil.format("https://apps.apple.com/{}/app/id{}", areaEnum.getCode(), appId);
                ForestResponse<?> response = Forest.get(appStoreUrl)
                    .autoRedirects(true)
                    .onSuccess(((data, req, res) -> log.info("{}-{}:success", appId, areaEnum.getCode())))
                    .onError(((ex, req, res) -> log.error("{}-{}:failed", appId, areaEnum.getCode())))
                    .execute(ForestResponse.class);
                if (response.getStatusCode() != HttpStatus.OK.value()) {
                    log.error("appId: {}, app not found in {} app store", appId, areaEnum.getCode());
                    return;
                }
                String html = response.readAsString();
                // 解析HTML
                Document doc = Jsoup.parse(html);
                // 构造出参
                GetAppInfoResDTO resDTO = new GetAppInfoResDTO();
                resDTO.setAppId(appId);
                resDTO.setArea(areaEnum.getCode());
                resDTO.setAreaName(areaEnum.getName());
                Element scriptElement = doc.selectFirst("#serialized-server-data");
                if (Objects.isNull(scriptElement)) {
                    log.error("appId: {}, area: {}, script element not found", appId, areaEnum.getCode());
                    return;
                }
                JSONObject jsonResult = JSON.parseArray(scriptElement.html().trim()).getJSONObject(0).getJSONObject("data");
                // 提取应用名称
                resDTO.setName(jsonResult.getString("title"));
                // 提取副标题
                resDTO.setSubTitle(jsonResult.getJSONObject("lockup").getString("subTitle"));
                // 提取开发者信息
                resDTO.setDeveloper(jsonResult.getJSONObject("developerAction").getString("title"));
                resDTO.setAppStoreUrl(appStoreUrl);
                // 提取价格信息
                resDTO.setPrice(parsePrice(jsonResult.getJSONObject("lockup").getJSONObject("buttonAction").getString("priceFormatted"), areaEnum));
                // 查找所有内购列表项
                JSONArray inAppPurchaseArray = jsonResult.getJSONObject("shelfMapping").getJSONObject("information").getJSONArray("items")
                    .toList(JSONObject.class)
                    .stream()
                    .filter(item -> areaEnum.getInAppPurchaseStr().equals(item.getString("title")))
                    .findFirst()
                    .map(item -> item.getJSONArray("items"))
                    .map(item -> item.getJSONObject(0))
                    .map(item -> item.getJSONArray("textPairs"))
                    .orElse(new JSONArray());
                // 存储解析结果
                List<InAppPurchaseDTO> inAppPurchaseList = new ArrayList<>();
                for (int i = 0; i < inAppPurchaseArray.size(); i++) {
                    JSONArray jsonArray = inAppPurchaseArray.getJSONArray(i);
                    InAppPurchaseDTO purchaseDTO = new InAppPurchaseDTO();
                    purchaseDTO.setObject(jsonArray.getString(0));
                    purchaseDTO.setPrice(parsePrice(jsonArray.getString(1), areaEnum));
                    inAppPurchaseList.add(purchaseDTO);
                }
                resDTO.setInAppPurchaseList(inAppPurchaseList);
                resultList.get().add(resDTO);
            });
            // 有售价的应用，按价格升序，有内购的应用，按内购价格升序
            resultList.set(resultList.get().stream()
                .sorted(Comparator.comparing(item -> item.getPrice().getCnyPrice()))
                .sorted(Comparator.comparing(
                    item -> item.getInAppPurchaseList().stream()
                        .min(Comparator.comparing(ele -> ele.getPrice().getCnyPrice()))
                        .orElse(InAppPurchaseDTO.none())
                        .getPrice().getCnyPrice()))
                .collect(Collectors.toList()));
            APP_INFO_CACHE.put(appId, resultList.get());
        }
        return APP_INFO_CACHE.get(appId);
    }

    /**
     * parse price
     *
     * @param priceStr price str
     * @param areaEnum area enum
     * @return {@link Money }
     */
    private Money parsePrice(String priceStr, AreaEnum areaEnum) {
        if (StrUtil.isBlank(priceStr)) {
            return new Money(areaEnum.getCurrencyCode(), BigDecimal.ZERO);
        }
        priceStr = priceStr.replace(areaEnum.getThousandsSeparator(), StrUtil.EMPTY);
        if (StrUtil.DOT.equals(areaEnum.getThousandsSeparator())) {
            priceStr = priceStr.replace(StrUtil.COMMA, StrUtil.DOT);
        }
        priceStr = ReUtil.get("\\d+(\\.\\d+)?", priceStr, 0);
        return new Money(areaEnum.getCurrencyCode(), new BigDecimal(priceStr).setScale(2, RoundingMode.HALF_UP));
    }

    /**
     * get area list
     *
     * @return {@link List }<{@link AreaResDTO }>
     */
    public List<AreaResDTO> getAreaList() {
        return Arrays.stream(AreaEnum.values()).map(item -> {
            AreaResDTO resDTO = new AreaResDTO();
            resDTO.setCode(item.getCode());
            resDTO.setName(item.getName());
            return resDTO;
        }).collect(Collectors.toList());
    }

}
