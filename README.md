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

Docker 一键运行（推荐）
1. 本仓库已包含 `docker-compose.yml`、`backend/Dockerfile`、`frontend/Dockerfile`，可用 Docker Compose 一键构建并启动整个系统（包含 PostGIS）：

   # 在仓库根目录运行（需要 Docker + Docker Compose）
   docker-compose up --build

2. 服务启动后：
   - PostGIS: localhost:5432 (user=postgres password=postgres, db=gislib)
   - 后端: http://localhost:8080
   - 前端: http://localhost:5173 (静态资源由 nginx 提供，映射到容器 80 端口)

注意：Compose 使用官方 `postgis/postgis:13-3.3` 镜像；如需不同版本请编辑 `docker-compose.yml` 中的 image 字段。

测试与实用工具
----------------

本仓库包含几个用于自动化测试与诊断的小脚本与容器：

- 集成测试容器 (`tester`)：
   - 在 `docker-compose.yml` 中已添加 `tester` 服务（基于 `scripts/Dockerfile`），用于在 Compose 网络内部运行后端集成测试。
   - 启动并运行测试：
      ```bash
      docker-compose up --build tester
      ```
      或先构建再运行：
      ```bash
      docker-compose build tester
      docker-compose run --rm tester
      ```
   - `tester` 会执行 `scripts/test_backend.py`：该脚本等待后端与 Postgres 可用，依次执行创建 library/book、插入 student、borrow/return 操作，并直接连接数据库验证表中数据变更。
   - 默认环境变量（可在 `docker-compose.yml` 中或 CLI 中覆盖）：
      - BACKEND_URL=http://backend:8080/api
      - PG_HOST=db
      - PG_PORT=5432
      - PG_USER=postgres
      - PG_PASS=postgres
      - PG_DB=gislib

- 本地运行测试脚本：
   - 你也可以在宿主机上直接运行测试脚本（需要 Python 与依赖）：
      ```bash
      pip install -r scripts/requirements.txt
      python3 scripts/test_backend.py
      ```
   - 可通过环境变量覆盖默认地址/凭据，例如：
      ```bash
      BACKEND_URL=http://localhost:8080/api PG_HOST=127.0.0.1 PG_PORT=5432 python3 scripts/test_backend.py
      ```

- 解析 Compose 文件工具：
   - `scripts/parse_compose.py` 会读取仓库根目录下的 `docker-compose.yml`，并输出 `db`、`backend`、`frontend` 服务的宿主机端口(Host port)与容器端口(Container port)，以便快速确认如何从宿主机访问各服务。
   - 用法：
      ```bash
      pip install pyyaml
      python3 scripts/parse_compose.py docker-compose.yml
      ```

数据与清理
-----------
- 集成测试脚本会在数据库中插入测试记录（library、book、student、rent）。目前脚本不会自动删除这些测试数据；若你希望每次运行后自动清理，请告诉我，我可以把脚本改为在结束时删除测试记录或使用事务/临时库。 

注意事项与建议
----------------
- SRID: 本项目默认使用 SRID=4523（见 `db/schema.sql` 与 `Library` 实体），请确保你的 PostGIS 环境支持该 SRID，或根据需要调整为 4326（WGS84）。
- 配置：为了在不同环境（本地开发、容器、部署）间切换，建议将后端 API 地址与 SRID 等参数通过环境变量暴露并在前端/后端中读取。

如需我把集成测试改为在结束后自动清理测试数据，或把测试迁移为 Spring Boot 原生集成测试（`mvn test`），回复告诉我你偏好的方案，我会继续实现。

