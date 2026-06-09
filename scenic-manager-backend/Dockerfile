# 多阶段构建：构建阶段
FROM --platform=linux/amd64 amazoncorretto:17-alpine AS builder

# 设置工作目录
WORKDIR /app

# 安装 Maven
RUN apk add --no-cache maven

# 复制 Maven 配置文件和源代码
COPY pom.xml .
COPY src ./src

# 构建应用（跳过测试以加快构建速度，显示详细输出）
RUN mvn clean package -DskipTests -X

# 运行阶段
FROM --platform=linux/amd64 amazoncorretto:17-alpine

# 安装必要的工具（用于健康检查）
RUN apk add --no-cache curl

# 设置工作目录
WORKDIR /app

# 从构建阶段复制 JAR 文件
COPY --from=builder /app/target/*.jar app.jar

# 暴露端口
EXPOSE 8088

# 启动应用
CMD ["java", "-jar", "app.jar"]