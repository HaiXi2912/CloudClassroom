# 01 后端启动与 Swagger

## 1. 启动前准备

- 已完成数据库初始化（cloud_classroom 已建库建表）
- 确保 MySQL 可用：`root/123456`，端口 `3306`

## 2. 使用项目自带 JDK17 + Maven 运行

在 PowerShell 执行：

```powershell
cd /d d:\CloudClassroom
.\scripts\env.ps1

# 使用项目级 Maven 镜像配置构建
.\.devtools\maven\maven-3.9.12\bin\mvn.cmd -s tools\maven\settings.xml -f backend\pom.xml -DskipTests package

# 启动
.\.devtools\maven\maven-3.9.12\bin\mvn.cmd -s tools\maven\settings.xml -f backend\pom.xml spring-boot:run
```

## 3. Swagger 入口

- Swagger UI：`http://localhost:8080/swagger-ui/index.html`
- OpenAPI JSON：`http://localhost:8080/v3/api-docs`

## 4. 最小可用接口（用于验证）

- `POST /api/auth/register`
- `POST /api/auth/login`
- `GET /api/auth/me`

> 注意：`/api/auth/me` 需要携带 Header：`Authorization: Bearer <token>`
