# 第一阶段：构建前端应用
FROM --platform=linux/amd64 node:20-alpine AS builder

# 设置工作目录
WORKDIR /app

# 复制package文件
COPY package*.json ./

# 安装依赖（使用国内镜像加速）
RUN npm config set registry https://registry.npmmirror.com && \
    npm install --production=false

# 复制源代码
COPY . .

# 构建生产版本（显示详细输出以便调试）
RUN npm run build

# 第二阶段：使用Nginx服务静态文件
FROM nginx:alpine

# 复制构建产物到Nginx
COPY --from=builder /app/dist /usr/share/nginx/html

# 复制Nginx配置
COPY nginx.conf /etc/nginx/nginx.conf
COPY default.conf /etc/nginx/conf.d/default.conf

# 确保必要的目录存在并设置权限
RUN mkdir -p /var/log/nginx /var/cache/nginx && \
    chown -R nginx:nginx /var/log/nginx /var/cache/nginx /usr/share/nginx/html

# 暴露3000端口
EXPOSE 3000

# 启动Nginx
CMD ["nginx", "-g", "daemon off;"]
