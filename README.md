# App Store Price Tracker

一个用于查询和比较不同地区 App Store 应用价格的 Spring Boot 应用。

## 项目简介

App Store Price Tracker 是一个强大的工具，可以查询不同国家/地区 App Store 中应用的价格信息。支持多国货币查询，并提供价格比较功能。

## 功能特性

- 查询指定应用在不同国家/地区的名称、开发者、价格信息
- 支持多个国家/地区的价格查询（美国、中国、台湾、香港、日本、韩国、土耳其、尼日利亚、印度、巴基斯坦、巴西等）
- 内购项目价格查询
- 应用评分和描述信息
- 响应式设计，支持多终端访问
- 数据缓存机制，提高查询效率
- 首页展示站内热门应用或游戏前十名，搜索后切换为搜索结果
- 按应用累计有效查询点击，支持重复请求去重和详情失败重试
- 支持 Docker 部署

## 技术栈

### 核心技术
- **编程语言**: Java 21
- **框架**: SpringBoot 4
- **构建工具**: Maven

### 主要依赖
- **Web 框架**: spring-boot-starter-web
- **参数校验**: spring-boot-starter-validation
- **代码简化**: Lombok
- **工具库**:
  - Hutool - 综合性工具库
- **JSON 处理**: Fastjson2
- **HTML 解析**:
  - JsoupXpath - 基于 Jsoup 的 XPath 解析
  - Jsoup - HTML 文档解析

### 前端
- HTML5, CSS3, JavaScript
- 响应式设计，支持多终端访问

### 容器化
- Docker
- Docker Compose

## 安装与部署

### Docker 部署（推荐）

```bash
docker run -d -p 8080:8080 ghcr.io/hypooo/app-store-price:latest
```

或使用 Docker Compose：

```bash
docker compose up -d
```

访问 `http://localhost:8080`

### 本地运行

1. 克隆项目
```bash
git clone https://github.com/hypooo/app-store-price.git
cd app-store-price
```

2. 使用 Maven 构建项目
```bash
mvn clean package
```

3. 运行应用
```bash
java -jar target/app-store-price-x.x.x.jar
```

4. 访问应用
打开浏览器访问 `http://localhost:8080`

## 首页与搜索交互

- 初次打开时搜索框为空，列表展示“热门排行”，应用与游戏合并排序。
- 排名全站统一，应用名称和简介按所选地区展示；在首页切换地区时刷新文案。
- 输入框聚焦时直接输入，移除热门搜索词下拉；提交搜索后，列表切换为搜索结果。
- 点击应用进入全球比价；返回时恢复原来的热门列表或搜索结果。
- 清空搜索会恢复并刷新热门列表；没有匹配结果时显示独立的无结果提示。
- 热门列表加载、加载失败和暂无记录分别展示对应状态，不影响主动搜索。

## 热门统计

搜索结果和热门排行的应用点击都参与计数。计数发生在外部详情请求入口，按 `appId + clickId` 去重并原子累计。底层详情获取方法只读取数据，避免价格对比内部调用详情方法时重复计数。详情缓存命中的新点击仍计数，失败查询不计数。

排行名称和简介复用成功详情查询返回的各地区数据，按应用和地区分别保存，不会被其他地区的搜索文案覆盖。单个地区查询失败时保留此前成功获取的文案；所选地区没有记录时，优先使用美区，再按固定地区顺序回退。返回排行只读取内存中的数据，不额外查询 App Store。

地区文案只保留当前前十名应用，按现有 13 个地区计算最多 130 份。应用落榜后清理文案，再次入榜时从本次成功详情中重建；所有应用的累计点击数和基础信息继续保留。

去重记录保留 24 小时；超过保留期再次使用同一点击标识可能重新计数。不同点击使用不同标识，因此用户有意再次查询会累计新的点击次数。

榜单和去重记录采用单实例内存存储，与现有缓存保持一致。服务重启后清空，多实例之间不共享统计数据。

## 缓存机制

应用使用 Hutool 缓存库实现数据缓存，有效减少对 App Store API 的重复请求：
- 应用列表缓存：1天
- 应用详情缓存：1天
- 使用细粒度锁机制，避免缓存穿透和并发问题

## 注意事项

1. 本应用依赖于 App Store 的公开 API 和网页解析，如果 Apple 修改其 API 或页面结构，可能需要相应更新解析逻辑。
2. 请遵守 Apple 的服务条款，合理使用查询功能，避免过于频繁的请求。
3. 汇率数据使用实时转换，实际价格可能与 App Store 显示略有差异。

## Star History

<a href="https://www.star-history.com/?repos=hypooo%2Fapp-store-price&type=date&legend=top-left">
 <picture>
   <source media="(prefers-color-scheme: dark)" srcset="https://api.star-history.com/image?repos=hypooo/app-store-price&type=date&theme=dark&legend=top-left" />
   <source media="(prefers-color-scheme: light)" srcset="https://api.star-history.com/image?repos=hypooo/app-store-price&type=date&legend=top-left" />
   <img alt="Star History Chart" src="https://api.star-history.com/image?repos=hypooo/app-store-price&type=date&legend=top-left" />
 </picture>
</a>
