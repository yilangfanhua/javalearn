# 1. 基础镜像：带 JDK 11 的运行环境
FROM eclipse-temurin:11-jre

# 2. 设置工作目录
WORKDIR /app

# 3. 把本地 jar 复制进镜像
COPY target/spring-demo-0.0.1-SNAPSHOT.jar app.jar

# 4. 声明暴露 8080 端口
EXPOSE 8080

# 5. 容器启动时执行什么
ENTRYPOINT ["java", "-jar", "app.jar"]