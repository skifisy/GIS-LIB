-- DB 初始化脚本: 建议手动执行以确保 PostGIS 扩展可用
-- 用法:
-- psql -U postgres -d gislib -f db/schema.sql

CREATE EXTENSION IF NOT EXISTS postgis;

-- library 表: 包含几何点, SRID = 4523
CREATE TABLE IF NOT EXISTS library (
  id BIGSERIAL PRIMARY KEY,
  name TEXT,
  college TEXT,
  number_of_books BIGINT,
  location geometry(Point,4523)
);

-- book 表
CREATE TABLE IF NOT EXISTS book (
  id BIGSERIAL PRIMARY KEY,
  library_id BIGINT REFERENCES library(id),
  name TEXT,
  publication_time timestamp without time zone,
  author TEXT,
  press_id BIGINT
);

-- student 表
CREATE TABLE IF NOT EXISTS student (
  id BIGSERIAL PRIMARY KEY,
  name TEXT,
  pwd TEXT,
  phone TEXT,
  sex boolean
);

-- rent 表
CREATE TABLE IF NOT EXISTS rent (
  id BIGSERIAL PRIMARY KEY,
  book_id BIGINT REFERENCES book(id),
  library_id BIGINT REFERENCES library(id),
  student_id BIGINT REFERENCES student(id),
  rent_time timestamp without time zone,
  return_time timestamp without time zone
);
