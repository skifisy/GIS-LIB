#!/usr/bin/env bash
set -euo pipefail

# install-deps.sh
# Ubuntu 22.04 适用：安装 OpenJDK11, Maven, Node.js (18), PostgreSQL + PostGIS
# 然后为前端/后端安装项目依赖，并初始化数据库（执行 db/schema.sql）

# Determine SUDO variable: prefer sudo if available; if running as root leave empty
if command -v sudo >/dev/null 2>&1; then
  SUDO="sudo"
else
  SUDO=""
fi

# If not root and sudo not available, abort
if [ "${EUID:-$(id -u)}" -ne 0 ] && [ -z "$SUDO" ]; then
  echo "请以 root 身份或使用 sudo 运行此脚本（例如：sudo ./install-deps.sh）"
  exit 1
fi

export DEBIAN_FRONTEND=noninteractive

# Color helpers
GREEN="\e[32m"
YELLOW="\e[33m"
RED="\e[31m"
BLUE="\e[34m"
RESET="\e[0m"

echo -e "${BLUE}使用 SUDO=${SUDO:-'(none)'}${RESET}"

# Helper to run apt-get with or without sudo
APT_CMD="${SUDO:+$SUDO }apt-get"

echo -e "${BLUE}更新 apt 源...${RESET}"
${APT_CMD} update -y

apt_install_if_missing() {
  # usage: apt_install_if_missing pkg1 pkg2 ...
  local to_install=()
  for pkg in "$@"; do
    if dpkg -s "$pkg" >/dev/null 2>&1; then
      echo -e "${YELLOW}$pkg 已安装，跳过${RESET}"
    else
      to_install+=("$pkg")
    fi
  done
  if [ ${#to_install[@]} -gt 0 ]; then
    echo -e "${BLUE}安装: ${to_install[*]}${RESET}"
    ${APT_CMD} install -y --no-install-recommends "${to_install[@]}"
  fi
}

echo -e "${BLUE}安装基础工具（若尚未安装）...${RESET}"
apt_install_if_missing curl ca-certificates gnupg lsb-release software-properties-common build-essential apt-transport-https

# Install and generate locale to avoid perl locale warnings in some containers
echo -e "${BLUE}确保 locales 已安装并生成 en_US.UTF-8...${RESET}"
apt_install_if_missing locales
${SUDO:+$SUDO }locale-gen en_US.UTF-8 || true
${SUDO:+$SUDO }update-locale LANG=en_US.UTF-8 || true
export LANG=en_US.UTF-8
export LC_ALL=en_US.UTF-8

echo -e "${BLUE}安装 OpenJDK 11 和 Maven（若尚未安装）...${RESET}"
apt_install_if_missing openjdk-11-jdk maven

echo -e "${BLUE}安装 Node.js 18 (LTS)（若尚未安装）...${RESET}"
if command -v node >/dev/null 2>&1; then
  echo -e "${YELLOW}node 已安装：$(node -v)，跳过 Node 安装${RESET}"
else
  curl -fsSL https://deb.nodesource.com/setup_18.x | bash -
  apt_install_if_missing nodejs
fi

echo -e "${BLUE}安装 PostgreSQL 与 PostGIS（若尚未安装）...${RESET}"
apt_install_if_missing postgresql postgresql-contrib postgis

echo -e "${BLUE}启用并启动 PostgreSQL 服务（如果可用）...${RESET}"
if command -v systemctl >/dev/null 2>&1; then
  ${SUDO:+$SUDO }systemctl enable --now postgresql || true
else
  # systemd not available in some containers; try service, then pg_ctlcluster fallback
  ${SUDO:+$SUDO }service postgresql start || true
fi

# If postgres still not running, try pg_ctlcluster (Debian/Ubuntu helper)
POSTGRES_UP=false
if command -v pg_isready >/dev/null 2>&1; then
  if pg_isready -q; then POSTGRES_UP=true; fi
fi
if [ "$POSTGRES_UP" = false ]; then
  if command -v pg_ctlcluster >/dev/null 2>&1; then
    echo -e "${BLUE}尝试使用 pg_ctlcluster 启动 PostgreSQL 集群...${RESET}"
    for verdir in /usr/lib/postgresql/*; do
      ver=$(basename "$verdir")
      echo -e "${BLUE}尝试启动 PostgreSQL 版本 ${ver} 的 main 集群...${RESET}"
      ${SUDO:+$SUDO }pg_ctlcluster "$ver" main start || true
    done
  fi
fi

# Re-check if PostgreSQL is up
if command -v pg_isready >/dev/null 2>&1; then
  if pg_isready -q; then POSTGRES_UP=true; fi
fi
if [ "$POSTGRES_UP" = false ]; then
  echo -e "${YELLOW}警告：未能检测到正在运行的 PostgreSQL 服务；后续数据库创建可能失败。${RESET}"
fi

echo -e "${BLUE}创建数据库 'gislib' 并启用 PostGIS 扩展（若已存在则跳过）...${RESET}"

# Helper: run SQL string as postgres user and return output (uses -tA for clean output)
run_psql_sql() {
  local sql="$1"
  if [ -n "$SUDO" ]; then
    # sudo preserves argument quoting
    $SUDO -u postgres psql -tA -c "$sql"
  elif command -v runuser >/dev/null 2>&1; then
    runuser -u postgres -- psql -tA -c "$sql"
  elif [ "${EUID:-$(id -u)}" -eq 0 ]; then
    su - postgres -c "psql -tA -c \"$sql\""
  else
    echo -e "${RED}无法以 postgres 用户运行 psql（既非 root，且 sudo/runuser 不可用）${RESET}"
    return 1
  fi
}

# Helper: run a SQL file as postgres user against a database
run_psql_file() {
  local db="$1"; shift
  local file="$1"
  if [ -n "$SUDO" ]; then
    $SUDO -u postgres psql -d "$db" -f "$file"
  elif command -v runuser >/dev/null 2>&1; then
    runuser -u postgres -- psql -d "$db" -f "$file"
  elif [ "${EUID:-$(id -u)}" -eq 0 ]; then
    su - postgres -c "psql -d '$db' -f '$file'"
  else
    echo -e "${RED}无法以 postgres 用户运行 psql（既非 root，且 sudo/runuser 不可用）${RESET}"
    return 1
  fi
}

# check db exists (use run_psql_sql which returns rows)
if run_psql_sql "SELECT 1 FROM pg_database WHERE datname='gislib'" | grep -q 1; then
  echo -e "${YELLOW}数据库 gislib 已存在，跳过创建${RESET}"
else
  run_psql_sql "CREATE DATABASE gislib;"
fi
run_psql_sql "\c gislib; CREATE EXTENSION IF NOT EXISTS postgis;"

SQL_FILE="$(pwd)/db/schema.sql"
if [ -f "$SQL_FILE" ]; then
  echo -e "${BLUE}执行 schema.sql 初始化表结构...${RESET}"
  if command -v psql >/dev/null 2>&1; then
    # use run_psql_file helper to run file
    run_psql_file gislib "$SQL_FILE"
  else
    echo -e "${RED}psql 未找到，无法执行 SQL 文件${RESET}"
  fi
else
  echo -e "${YELLOW}找不到 db/schema.sql，跳过 SQL 初始化。${RESET}"
fi

echo "为前端安装依赖（如果存在）..."
if [ -d "frontend" ]; then
  echo -e "${BLUE}为前端安装依赖（如果存在）...${RESET}"
  cd frontend
  if command -v npm >/dev/null 2>&1; then
    echo -e "${BLUE}运行 npm install...${RESET}"
    npm install --no-audit --no-fund
  else
    echo -e "${RED}未检测到 npm，请检查 Node.js 安装。${RESET}"
  fi
  cd - >/dev/null
fi

echo "为后端预解析并打包（跳过测试）..."
if [ -d "backend" ]; then
  echo -e "${BLUE}为后端预解析并打包（跳过测试）...${RESET}"
  cd backend
  if command -v mvn >/dev/null 2>&1; then
    mvn -DskipTests package -e
  else
    echo -e "${RED}未检测到 mvn，请检查 Maven 安装。${RESET}"
  fi
  cd - >/dev/null
fi

echo "完成：依赖安装与初始化已结束。"
echo "后续步骤："
echo "  1) 编辑 backend/src/main/resources/application.properties，确认数据库连接信息（若非本机默认 postgres/postgres）。"
echo "  2) 启动后端：cd backend && mvn spring-boot:run"
echo "  3) 启动前端：cd frontend && npm run dev"
echo "数据库默认创建用户为系统 postgres 用户；若需创建带密码的 postgres 帐号，请使用 psql 手动配置并在 application.properties 中更新凭据。"
