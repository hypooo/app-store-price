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
- 支持 Docker 部署

## 技术栈

- **后端**: Spring Boot 4
- **编程语言**: Java 21
- **HTTP 客户端**: Forest
- **JSON 处理**: Fastjson2
- **HTML 解析**: JsoupXpath
- **工具库**: Hutool, Guava
- **构建工具**: Maven
- **前端**: HTML, CSS, JavaScript
- **容器化**: Docker

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

## API 接口

### 获取支持的地区列表
- **接口**: `POST /app/getAreaList`
- **请求体**: 无
- **响应**: 支持的国家/地区列表

### 搜索应用列表
- **接口**: `POST /app/getAppList`
- **请求体**:
```json
{
  "appName": "应用名称",
  "areaCode": "地区代码"
}
```
- **响应**: 应用列表信息

### 获取应用详细信息
- **接口**: `POST /app/getAppInfo`
- **请求体**:
```json
{
  "appId": "应用ID"
}
```
- **响应**: 应用详细信息（包括各地区价格）

## 项目结构

```
app-store-price/
├── src/
│   ├── main/
│   │   ├── java/com/hypo/appstoreprice/
│   │   │   ├── controller/      # 控制器层
│   │   │   ├── service/         # 服务层
│   │   │   ├── pojo/            # 数据传输对象
│   │   │   │   ├── bean/        # 基础数据对象
│   │   │   │   ├── enums/       # 枚举类
│   │   │   │   ├── request/     # 请求对象
│   │   │   │   └── response/    # 响应对象
│   │   │   ├── common/          # 通用工具类
│   │   │   ├── handler/         # 异常和结果处理器
│   │   │   └── AppStorePriceApplication.java  # 应用启动类
│   │   └── resources/
│   │       ├── application.yml # 应用配置文件
│   │       └── static/          # 静态资源
├── Dockerfile                   # Docker 构建文件
├── docker-compose.yml           # Docker Compose 配置
├── pom.xml                      # Maven 依赖配置
└── README.md                    # 项目说明文档
```

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

<picture>
   <source media="(prefers-color-scheme: dark)" srcset="https://api.star-history.com/svg?repos=hypooo/app-store-price&type=date&theme=dark&legend=top-left" />
   <source media="(prefers-color-scheme: light)" srcset="https://api.star-history.com/svg?repos=hypooo/app-store-price&type=date&legend=top-left" />
   <img alt="Star History Chart" src="https://api.star-history.com/svg?repos=hypooo/app-store-price&type=date&legend=top-left" />
</picture>
