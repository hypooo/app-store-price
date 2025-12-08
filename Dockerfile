# 使用 JDK 21 作为基础镜像
FROM amazoncorretto:21-alpine

# 设置环境变量
ENV LANG=C.UTF-8
ENV TZ=Asia/Shanghai

# 替换为国内源、安装 tzdata 配置时区，然后删除不必要文件
RUN apk add --no-cache tzdata && \
    cp /usr/share/zoneinfo/${TZ} /etc/localtime && \
    echo "${TZ}" > /etc/timezone && \
    apk del tzdata && \
    rm -rf /var/cache/apk/*

# 设置工作目录
WORKDIR /app

# 暴露端口 8080
EXPOSE 8080

# 复制 jar 文件到容器中
COPY ./target/app-store-price-*.jar ./app.jar

# 设置容器启动时执行的命令
ENTRYPOINT ["java", "-jar", "app.jar"]
