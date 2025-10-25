#!/usr/bin/env python3
"""
scripts/test_backend.py

集成测试后端 API 与数据库：
- 等待 backend HTTP 服务可用
- 创建一个图书馆 /api/libraries
- 连接 PostgreSQL 验证 `library` 表中记录
- 创建一本书 /api/books 并验证 `book` 表
- 在数据库插入一个 student，调用 /api/borrow，验证 `rent` 表
- 调用 /api/return，验证 return_time 已被设置

用法：
  pip install requests psycopg2-binary pyyaml
  python3 scripts/test_backend.py

请确保 docker-compose 已经启动并且端口映射为常见值（backend:8080, db:5432），否则使用环境变量调整。
"""
import time
import sys
import os
import json

import yaml
import requests
import psycopg2
from psycopg2.extras import RealDictCursor

# Config (可通过环境变量覆盖)
BACKEND_URL = os.environ.get('BACKEND_URL', 'http://localhost:8083/api')
PG_HOST = os.environ.get('PG_HOST', 'localhost')
PG_PORT = int(os.environ.get('PG_PORT', '5432'))
PG_USER = os.environ.get('PG_USER', 'postgres')
PG_PASS = os.environ.get('PG_PASS', 'postgres')
PG_DB = os.environ.get('PG_DB', 'gislib')

def wait_for_http(url, timeout=60):
    deadline = time.time() + timeout
    while time.time() < deadline:
        try:
            r = requests.get(url, timeout=3)
            if r.status_code < 500:
                return True
        except Exception:
            pass
        time.sleep(1)
    return False

def wait_for_pg(timeout=60):
    deadline = time.time() + timeout
    while time.time() < deadline:
        try:
            conn = psycopg2.connect(host=PG_HOST, port=PG_PORT, user=PG_USER, password=PG_PASS, dbname=PG_DB)
            conn.close()
            return True
        except Exception:
            time.sleep(1)
    return False

def pg_conn():
    return psycopg2.connect(host=PG_HOST, port=PG_PORT, user=PG_USER, password=PG_PASS, dbname=PG_DB)

def expect(cond, msg):
    if cond:
        print('[OK]', msg)
    else:
        print('[FAIL]', msg)
        sys.exit(2)

def main():
    print('TEST: 等待后端服务...', BACKEND_URL)
    ok = wait_for_http(BACKEND_URL + '/libraries', timeout=60)
    expect(ok, 'backend HTTP 可用')

    print('TEST: 等待 Postgres 服务...')
    ok = wait_for_pg(timeout=60)
    expect(ok, 'Postgres 可用')

    # 1) 创建 library
    lib_payload = {
        'name': 'TEST_LIB_py',
        'college': '测试学院',
        'numberOfBooks': 123,
        'lon': 104.07,
        'lat': 30.67
    }
    print('TEST: POST /libraries ->', lib_payload)
    r = requests.post(BACKEND_URL + '/libraries', json=lib_payload, timeout=10)
    expect(r.status_code == 200, f'创建 library 返回 200 (实际 {r.status_code})')
    lib = r.json()
    lib_id = lib.get('id')
    expect(lib_id is not None, '创建 library 返回 id')

    # Verify in DB
    conn = pg_conn()
    try:
        cur = conn.cursor(cursor_factory=RealDictCursor)
        cur.execute('SELECT * FROM library WHERE id = %s', (lib_id,))
        row = cur.fetchone()
        expect(row is not None and row['name'] == lib_payload['name'], '数据库中存在刚插入的 library')
    finally:
        conn.close()

    # 2) 创建 book
    book_payload = {
        'libraryId': lib_id,
        'name': 'TEST_BOOK_py',
        'author': '测试作者'
    }
    print('TEST: POST /books ->', book_payload)
    r = requests.post(BACKEND_URL + '/books', json=book_payload, timeout=10)
    expect(r.status_code == 200, f'创建 book 返回 200 (实际 {r.status_code})')
    book = r.json()
    book_id = book.get('id')
    expect(book_id is not None, '创建 book 返回 id')

    # Verify book in DB
    conn = pg_conn()
    try:
        cur = conn.cursor(cursor_factory=RealDictCursor)
        cur.execute('SELECT * FROM book WHERE id = %s', (book_id,))
        row = cur.fetchone()
        expect(row is not None and row['name'] == book_payload['name'], '数据库中存在刚插入的 book')
    finally:
        conn.close()

    # 3) 在 DB 中插入 student
    conn = pg_conn()
    try:
        cur = conn.cursor()
        cur.execute("INSERT INTO student (name, pwd, phone, sex) VALUES (%s, %s, %s, %s) RETURNING id",
                    ('TEST_STUDENT_py', 'pwd', '123456', True))
        sid = cur.fetchone()[0]
        conn.commit()
        expect(sid is not None, '数据库中插入 student 并返回 id')
    finally:
        conn.close()

    # 4) borrow
    print('TEST: POST /borrow -> bookId=%s, studentId=%s' % (book_id, sid))
    r = requests.post(BACKEND_URL + '/borrow', json={'bookId': book_id, 'studentId': sid}, timeout=10)
    expect(r.status_code == 200, f'borrow 返回 200 (实际 {r.status_code})')
    rent = r.json()
    rent_id = rent.get('id')
    expect(rent_id is not None, 'borrow 返回 rent id')

    # Verify rent record in DB
    conn = pg_conn()
    try:
        cur = conn.cursor(cursor_factory=RealDictCursor)
        cur.execute('SELECT * FROM rent WHERE id = %s', (rent_id,))
        row = cur.fetchone()
        expect(row is not None and row['book_id'] == book_id and row['student_id'] == sid, 'rent 记录在数据库中')
    finally:
        conn.close()

    # 5) return
    print('TEST: POST /return -> rentId=%s, studentId=%s' % (rent_id, sid))
    r = requests.post(BACKEND_URL + '/return', json={'rentId': rent_id, 'studentId': sid}, timeout=10)
    expect(r.status_code == 200, f'return 返回 200 (实际 {r.status_code})')

    # Verify return_time set
    conn = pg_conn()
    try:
        cur = conn.cursor(cursor_factory=RealDictCursor)
        cur.execute('SELECT * FROM rent WHERE id = %s', (rent_id,))
        row = cur.fetchone()
        expect(row is not None and row['return_time'] is not None, 'rent.return_time 已设置')
    finally:
        conn.close()

    print('\nALL TESTS PASSED')


if __name__ == '__main__':
    try:
        main()
    except Exception as e:
        print('Error during tests:', e)
        sys.exit(3)
