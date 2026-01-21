# GitHub 部署（前端 + 后端）

> 结论先说：
> - **前端**可以直接部署到 **GitHub Pages**。
> - **后端（Spring Boot）无法“跑在 GitHub Pages/仓库上”**，GitHub 主要用于托管代码与 CI；后端需要部署到 Render/Railway/Fly.io/云服务器等。
> - 本仓库已加入 GitHub Actions：
>   - 前端：自动构建并部署到 GitHub Pages
>   - 后端：自动构建 jar 并上传为 Actions 构建产物

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

### 2.2 配置后端 API 地址（推荐）
因为 GitHub Pages 是静态站点，前端请求 `/api/...` 需要指向你真正部署的后端域名。

在 GitHub 仓库：Settings → Secrets and variables → Actions → **Variables** 添加：
- `VITE_API_BASE`：例如 `https://your-backend.onrender.com`

然后 push 一次代码，Actions 会自动部署。

## 3. 后端如何“部署并对接前端”
本仓库已提供 workflow：`.github/workflows/backend-ci.yml`
- push 后会自动 `mvn clean package` 并上传 jar（Artifacts）。

### 3.1 推荐的托管方式（任选其一）
- Render / Railway：直接连接 GitHub 仓库部署（最省事）
- Fly.io：Docker 部署
- 你自己的云服务器：上传 jar 运行

### 3.2 用 Render 部署后端（适合第一次上云）
下面步骤以你的仓库为例（`https://github.com/cky864847532/my.git`）。

1) 打开 Render 控制台 → New → **Web Service**
2) 选择 **Build and deploy from a Git repository** → 连接你的 GitHub
3) 选中仓库 `cky864847532/my`
4) 在创建页面填写（关键）：
	- **Root Directory**：`backend`
	- **Environment**：`Java`
	- **Build Command**：`mvn -B clean package -DskipTests`
	- **Start Command**：`java -jar target/*.jar`
5) Environment Variables（至少建议设置这些）：
	- `SPRING_PROFILES_ACTIVE`：`render`
	- `CORS_ALLOWED_ORIGINS`：`https://cky864847532.github.io`
	- `DEEPSEEK_API_KEY`：可选（不配也能跑，AI 接口返回 mock）
6) 点击 Create Web Service，等待首次 Build/Deploy 完成

部署成功后，你会得到一个后端地址，例如：`https://xxx.onrender.com`
把它填到 GitHub Actions 变量 `VITE_API_BASE`（前端会用它来请求 `/api/...`）。

> 说明：本项目已提供 `application-render.yml`，默认用 H2 文件库先跑通部署流程。
> 如果你要接 MySQL，请在 Render 里配置 `DB_URL/DB_USERNAME/DB_PASSWORD` 并把 profile 改成你自己的生产配置。

### 3.3 后端环境变量（部署时必配）
后端配置已改为环境变量占位（见 `backend/src/main/resources/application.yml`）：
- `DB_URL`
- `DB_USERNAME`
- `DB_PASSWORD`
- `DEEPSEEK_API_KEY`（可选）
- `CORS_ALLOWED_ORIGINS`（建议填你的 Pages 域名，例如 `https://<user>.github.io`）

## 4. 为什么前端路由改成 hash
GitHub Pages 不支持 SPA 的 history 路由重写（直接访问 `/products` 会 404）。
当前已切为 hash 模式：`/#/products`，以保证部署后“刷新/直达链接”可用。
