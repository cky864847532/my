# GitHub Pages + 阿里云 ECS（Docker）部署

> 结论先说：
> - **前端**部署到 **GitHub Pages**。
> - **后端**使用 **Docker 部署到阿里云 ECS**。
> - 本仓库已加入 GitHub Actions：
>   - 前端：自动构建并部署到 GitHub Pages
>   - 后端：自动构建 jar 并上传为 Actions 构建产物（可选用）

## 1. 把代码推到 GitHub
1) 在 GitHub 创建一个新仓库（建议 public/private 都行）。
2) 在本地（仓库根目录）执行：
- `git init`
- `git add .`
- `git commit -m "init"`
- `git branch -M main`
- `git remote add origin <你的仓库地址>`
- `git push -u origin main`

## 2. 部署前端到 GitHub Pages
本项目已提供 workflow：`.github/workflows/frontend-pages.yml`

### 2.1 开启 Pages（必须做一次）
GitHub 仓库 → Settings → Pages → Build and deployment 选择 **GitHub Actions**。

### 2.2 配置后端 API 地址（必须）
因为 GitHub Pages 是静态站点，前端请求 `/api/...` 需要指向你真正部署的后端域名（ECS 公网域名或 IP）。

在 GitHub 仓库：Settings → Secrets and variables → Actions → **Variables** 添加：
- `VITE_API_BASE`：例如 `http://<你的ECS公网IP>:8080`

然后 push 一次代码，Actions 会自动部署。

### 2.3 GitHub Pages 路由说明
本项目已改为 **hash 路由**，访问形如 `/#/products`，以避免 GitHub Pages 直达刷新 404。

## 3. 后端使用 Docker 部署到阿里云 ECS

### 3.1 ECS 服务器准备
1) 在阿里云控制台放通安全组端口：
	- 80/443（如果你后续接 Nginx/HTTPS）
	- 8080（直接暴露后端时）
2) 在 ECS 上安装 Docker（CentOS/Ubuntu 任意）：
	- 安装后确保 `docker` 命令可用。

### 3.2 构建镜像
本仓库已新增 [backend/Dockerfile](backend/Dockerfile)，可直接构建：
```bash
cd backend
docker build -t kailv-backend:latest .
```

### 3.3 运行容器（推荐使用环境变量）
建议用 Spring Boot 标准环境变量覆盖数据库/跨域/AI Key：

```bash
docker run -d --name kailv-backend \
  -p 8080:8080 \
  -e SPRING_DATASOURCE_URL="jdbc:mysql://<你的DB主机>:3306/<库名>?useUnicode=true&characterEncoding=utf8&serverTimezone=Asia/Shanghai" \
  -e SPRING_DATASOURCE_USERNAME="<DB用户名>" \
  -e SPRING_DATASOURCE_PASSWORD="<DB密码>" \
  -e AI_DEEPSEEK_API_KEY="<可选>" \
  -e APP_CORS_ALLOWED_ORIGINS="https://<user>.github.io" \
  kailv-backend:latest
```

> 说明：
> - `APP_CORS_ALLOWED_ORIGINS` 支持多个域名，用逗号分隔。
> - 如果你要用 IP 访问前端或自定义域名，请对应修改 CORS。

### 3.4 （可选）使用 Nginx 反向代理
如需 HTTPS 或隐藏端口，可在 ECS 上用 Nginx 反代 8080 到 80/443。

## 4. 对接前端与后端
1) 在 GitHub 仓库 Actions Variables 中设置 `VITE_API_BASE` 为你的后端地址（如 `http://<ECS公网IP>:8080`）。
2) 重新 push 触发 GitHub Pages 构建。
3) 访问 Pages 域名验证接口联通。
