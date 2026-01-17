# --- 阶段 1: 构建阶段 (Build Stage) ---
# 使用带有 Maven 的 JDK 21 镜像进行编译
FROM maven:3.9.6-eclipse-temurin-21-alpine AS build
WORKDIR /app

# 1. 技巧：先只复制 pom.xml 来下载依赖（利用 Docker 缓存，避免重复下载）
COPY pom.xml .
RUN mvn dependency:go-offline

# 2. 复制源码并进行打包
COPY src ./src
RUN mvn clean package -DskipTests

# --- 阶段 2: 运行阶段 (Run Stage) ---
# 使用更小、更安全的 JRE 21 运行环境
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# 从构建阶段复制生成的指定 JAR 包
COPY --from=build /app/target/JavaSprint4_2CRUDLevel3MongoDB-0.0.1-SNAPSHOT.jar app.jar

# 暴露端口
EXPOSE 8080

# 启动命令
ENTRYPOINT ["java", "-jar", "app.jar"]