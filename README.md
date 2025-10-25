GIS-based Distributed Library Management System

概述：这是一个分布式图书管理系统，该系统使用GIS将各个高校的图书馆连接起来，并支持管理员在地图上选点添加图书馆、在地图上显示、以及学生借/还书的后端接口。

目录结构（简要）：
- backend: Spring Boot 后端（JPA + PostGIS）
- frontend: Vue3 + Vite 前端（Leaflet 地图）
- db/schema.sql: 初始化数据库与表结构（包括 PostGIS 扩展）


Docker 一键运行
1. 本仓库已包含 `docker-compose.yml`、`backend/Dockerfile`、`frontend/Dockerfile`，可用 Docker Compose 一键构建并启动整个系统（包含 PostGIS）：

```sh
   docker-compose up --build
```

> 注意：请使用合适的镜像源安装对应的镜像！

2. 服务启动后：
   - PostGIS: localhost:5432 (user=postgres password=postgres, db=gislib)
   - 后端: http://localhost:8083
   - 前端: http://localhost:5173 (静态资源由 nginx 提供，映射到容器 80 端口)