GIS-based Distributed Library Management System (Minimal Prototype)

说明：这是一个最小可运行的原型，演示如何把高校图书馆用 GIS（PostGIS + Leaflet）连接起来，并支持管理员在地图上选点添加图书馆、在地图上显示、以及学生借/还书的后端接口。

目录结构（简要）：
- backend: Spring Boot 后端（JPA + PostGIS）
- frontend: Vue3 + Vite 前端（Leaflet 地图）
- db/schema.sql: 初始化数据库与表结构（包括 PostGIS 扩展）

快速开始（概要步骤）
1. 安装 PostgreSQL 与 PostGIS，并创建数据库 `gislib`：
   - 创建数据库并启用 PostGIS：
     psql -U postgres -c "CREATE DATABASE gislib;"
     psql -U postgres -d gislib -c "CREATE EXTENSION postgis;"
   - 修改 `backend/src/main/resources/application.properties` 中的数据源用户名/密码/URL 如有需要。

2. 后端：
   - 进入 `backend/` 使用 Maven 构建并运行（需要 JDK 11+ 与 Maven）：
     mvn spring-boot:run

3. 前端：
   - 进入 `frontend/` 安装依赖并运行（需要 Node.js + npm/yarn）：
     npm install
     npm run dev

4. 浏览器访问前端 dev 地址（默认为 http://localhost:5173），打开地图：
   - 点击右上角切换为 Admin 模式，点击地图新增图书馆，填写名称/学院/藏书数，提交后会 POST 到后端并在地图上显示。

说明与限制：
- 这是一个教学/演示级别的最小实现，未加入完整认证、并发/事务细化、复杂权限控制、输入校验与前端样式美化。
- SRID 使用题目要求的 4523（请确保你的 PostGIS 支持该 SRID 或调整为 4326）。

后续工作建议：
- 加入学生登录与会话管理（JWT），管理员权限校验。
- 为借还书操作添加业务校验（例如判断书籍是否可借、书籍库存实时更新）。
- 支持空间查询（附近图书馆、范围内图书）及图层样式定制。
