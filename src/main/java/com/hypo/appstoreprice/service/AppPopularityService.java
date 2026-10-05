package com.hypo.appstoreprice.service;

import com.hypo.appstoreprice.pojo.enums.AreaEnum;
import com.hypo.appstoreprice.pojo.response.GetAppInfoResDTO;
import com.hypo.appstoreprice.pojo.response.GetAppListResDTO;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.*;

/**
 * 站内热门应用排行
 * 元数据、累计次数和点击去重记录仅保存在当前进程内存中。
 *
 * @author hypo
 * @date 2026-09-30
 */
@Service
public class AppPopularityService {

    private static final int POPULAR_APP_LIMIT = 10;

    private static final long SEARCH_METADATA_TTL_NANOS = Duration.ofDays(1).toNanos();

    // 已累计应用的元数据随累计榜单保留。
    private final Map<String, AppMetadata> appMetadataById = new HashMap<>();

    // 只保留当前前十名的地区文案，新点击更新成功返回的地区。
    private final Map<String, Map<String, AppText>> appTextsByAppId = new HashMap<>();

    // 尚未点击的搜索结果只暂存 24 小时。
    private final Map<String, SearchMetadata> pendingMetadataById = new LinkedHashMap<>();

    private final Map<String, Long> clickCountsByAppId = new HashMap<>();

    // 次数只增不减，每次点击只需更新该应用的排名，避免重新排序全部应用。
    private final NavigableSet<AppRank> popularAppRanks = new TreeSet<>(
        Comparator.comparingLong(AppRank::clickCount).reversed().thenComparing(AppRank::appId));

    // 与累计次数一起保留，避免同一指纹因去重记录过期而重复计数。
    private final Set<ClickKey> countedClicks = new HashSet<>();

    /**
     * 只暂存尚未累计点击的应用元数据，搜索不覆盖已累计应用的图标、平台和文案。
     */
    public synchronized void rememberSearchResults(List<GetAppListResDTO> appList) {
        long now = System.nanoTime();
        removeExpiredEntries(now);
        for (GetAppListResDTO app : appList) {
            if (appMetadataById.containsKey(app.getAppId())) {
                continue;
            }
            AppMetadata metadata = new AppMetadata(
                app.getAppId(), app.getAppImage(), app.getPlatform());
            // 更新插入顺序，保持过期清理可以从最早的记录开始。
            pendingMetadataById.remove(app.getAppId());
            pendingMetadataById.put(app.getAppId(), new SearchMetadata(metadata, now));
        }
    }

    /**
     * 记录一次已经成功获取详情的应用点击，搜索结果和热门排行均参与计数。
     * 同一 appId + fingerprint 在当前进程生命周期内只计数一次，缺失指纹不计数。
     * 复用本次详情的各地区名称和副标题，不额外请求 App Store。
     */
    public synchronized void recordAppClick(String appId, String fingerprint, List<GetAppInfoResDTO> appInfo) {
        if (fingerprint == null || fingerprint.isBlank() || appInfo == null || appInfo.isEmpty()) {
            return;
        }
        long now = System.nanoTime();
        removeExpiredEntries(now);

        // 未实际搜索到元数据的应用不进入排行。
        AppMetadata metadata = appMetadataById.get(appId);
        if (metadata == null) {
            SearchMetadata searchMetadata = pendingMetadataById.get(appId);
            if (searchMetadata != null) {
                metadata = searchMetadata.metadata();
            }
        }
        if (metadata == null) {
            return;
        }

        // 与累计操作处于同一同步区间，避免并发请求重复计数。
        ClickKey clickKey = new ClickKey(appId, fingerprint.trim());
        if (!countedClicks.add(clickKey)) {
            return;
        }

        long previousClickCount = clickCountsByAppId.getOrDefault(appId, 0L);
        long clickCount = clickCountsByAppId.merge(appId, 1L, Long::sum);
        appMetadataById.put(appId, metadata);
        pendingMetadataById.remove(appId);

        popularAppRanks.remove(new AppRank(appId, previousClickCount));
        AppRank updatedRank = new AppRank(appId, clickCount);
        popularAppRanks.add(updatedRank);
        if (popularAppRanks.size() > POPULAR_APP_LIMIT) {
            AppRank removedRank = popularAppRanks.pollLast();
            appTextsByAppId.remove(removedRank.appId());
        }
        if (!popularAppRanks.contains(updatedRank)) {
            return;
        }

        // 入榜时复用本次详情重建文案；仍在榜时保留暂未返回的地区文案。
        Map<String, AppText> appTextsByArea = new HashMap<>(appTextsByAppId.getOrDefault(appId, Map.of()));
        for (GetAppInfoResDTO info : appInfo) {
            appTextsByArea.put(info.getArea(), new AppText(info.getName(), info.getSubtitle()));
        }
        appTextsByAppId.put(appId, Map.copyOf(appTextsByArea));
    }

    /**
     * 按累计点击数取前十个应用，并按 appId 稳定处理同分应用。
     * 文案优先使用请求地区，缺失时按 AreaEnum 固定顺序回退。
     */
    public synchronized List<GetAppListResDTO> getPopularAppList(String areaCode) {
        removeExpiredEntries(System.nanoTime());
        return popularAppRanks.stream()
            .map(rank -> appMetadataById.get(rank.appId()).toResponse(getAppText(rank.appId(), areaCode)))
            .toList();
    }

    private AppText getAppText(String appId, String areaCode) {
        Map<String, AppText> appTextsByArea = appTextsByAppId.get(appId);
        AppText requestedText = appTextsByArea.get(areaCode);
        if (requestedText != null) {
            return requestedText;
        }
        return Arrays.stream(AreaEnum.values())
            .map(area -> appTextsByArea.get(area.getCode()))
            .filter(Objects::nonNull)
            .findFirst()
            .orElseThrow();
    }

    private void removeExpiredEntries(long now) {
        Iterator<Map.Entry<String, SearchMetadata>> metadataIterator = pendingMetadataById.entrySet().iterator();
        while (metadataIterator.hasNext()) {
            Map.Entry<String, SearchMetadata> entry = metadataIterator.next();
            if (now - entry.getValue().recordedAtNanos() < SEARCH_METADATA_TTL_NANOS) {
                break;
            }
            metadataIterator.remove();
        }
    }

    private record AppRank(String appId, long clickCount) {
    }

    private record ClickKey(String appId, String fingerprint) {
    }

    private record SearchMetadata(AppMetadata metadata, long recordedAtNanos) {
    }

    private record AppText(String appName, String appDesc) {
    }

    private record AppMetadata(String appId, String appImage, String platform) {

        private GetAppListResDTO toResponse(AppText appText) {
            GetAppListResDTO response = new GetAppListResDTO();
            response.setAppId(appId);
            response.setAppName(appText.appName());
            response.setAppImage(appImage);
            response.setAppDesc(appText.appDesc());
            response.setPlatform(platform);
            return response;
        }
    }

}
