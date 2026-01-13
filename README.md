# 云课堂（CloudClassroom）——前后端分离学生学习平台

> 目标：使用 SpringBoot 2.7 + MyBatis Plus + MySQL + Vue3 + Element Plus 实现一个类似“学习通”的学生学习平台。
> 要求：中文注释、中文文档、中文页面内容；尽量使用中国境内镜像源；数据库默认本地 3306，密码 123456。

## 目录结构（按模块分区）

- `backend/`：后端 SpringBoot 工程
- `frontend/`：前端 Vite + Vue3 工程
- `sql/`：数据库建库建表、初始化数据脚本
- `docs/`：全部项目文档（中文）
- `scripts/`：本地开发辅助脚本
- `tools/`：项目级工具配置（例如 Maven settings.xml）

## 快速开始（Windows）

1. 初始化当前终端的项目环境（JDK17 + Maven）

```powershell
cd /d d:\CloudClassroom
.\scripts\env.ps1
```

2. 阅读环境与镜像源说明

- [docs/01-环境与镜像源.md](docs/01-环境与镜像源.md)

> 后续当后端/前端脚手架创建完成后，我会补充“启动后端/启动前端/导入数据库”的一键命令。
