#!/usr/bin/env python3
"""
parse_compose.py

解析仓库根目录下的 docker-compose.yml 文件，输出 db、backend、frontend 的
宿主机端口（host port）与容器端口，并打印建议用来访问的地址。

用法:
  python3 scripts/parse_compose.py [path/to/docker-compose.yml]

如果缺少第三方库 pyyaml，会提示安装：pip install pyyaml
"""
import sys
import os

def try_import_yaml():
    try:
        import yaml
        return yaml
    except Exception:
        return None


def parse_ports_entry(entry):
    # entry could be str like "5432:5432" or "5173:80" or a dict/other
    if isinstance(entry, str):
        parts = entry.split(':')
        if len(parts) >= 2:
            host = parts[0]
            container = parts[-1]
            return host.strip(), container.strip()
        else:
            # only container port specified
            return None, parts[0].strip()
    elif isinstance(entry, int):
        return str(entry), str(entry)
    elif isinstance(entry, dict):
        # e.g. { "published": 5432, "target": 5432 }
        host = entry.get('published') or entry.get('published_port') or entry.get('host_port')
        container = entry.get('target') or entry.get('container_port') or entry.get('container')
        if host is None and container is not None:
            host = None
        return (str(host) if host is not None else None, str(container) if container is not None else None)
    else:
        return None, None


def main():
    yaml = try_import_yaml()
    if yaml is None:
        print("需要 pyyaml 库来解析 docker-compose.yml。请先运行: pip install pyyaml")
        sys.exit(2)

    path = sys.argv[1] if len(sys.argv) > 1 else 'docker-compose.yml'
    if not os.path.exists(path):
        print(f"未找到文件: {path}")
        sys.exit(1)

    with open(path, 'r', encoding='utf-8') as f:
        data = yaml.safe_load(f)

    services = data.get('services', {}) if isinstance(data, dict) else {}

    targets = ['db', 'backend', 'frontend']

    results = {}
    for name in targets:
        svc = services.get(name, {})
        ports = svc.get('ports', []) if isinstance(svc, dict) else []
        host_port = None
        container_port = None
        # choose first explicit host mapping if available
        for p in ports:
            h, c = parse_ports_entry(p)
            if h is not None:
                host_port = h
                container_port = c
                break
            elif container_port is None and c is not None:
                container_port = c

        results[name] = {
            'host_port': host_port,
            'container_port': container_port,
        }

    # 输出结果，说明哪个是宿主机端口
    print('解析结果:')
    for name in targets:
        r = results.get(name)
        print('\n' + name.upper() + ':')
        hp = r.get('host_port')
        cp = r.get('container_port')
        if hp:
            print(f"  宿主机端口 (host port): {hp}")
            print(f"  容器端口 (container port): {cp}")
            if name == 'backend':
                print(f"  Host access URL: http://localhost:{hp}")
                print(f"  Container network URL: http://{name}:{cp}")
            elif name == 'frontend':
                print(f"  Host access URL: http://localhost:{hp}")
                print(f"  Container network URL: http://{name}:{cp}")
            elif name == 'db':
                print(f"  Host access (Postgres): postgresql://localhost:{hp}")
                print(f"  Container network (Postgres): {name}:{cp}")
        else:
            if cp:
                print(f"  未映射到宿主机端口，容器端口为: {cp}")
                print(f"  Container network URL: {name}:{cp}")
            else:
                print("  未找到端口映射信息")

    # 简短提示
    print('\n注意: 左侧的端口映射格式通常为 "宿主机端口:容器端口"（host:container）。')


if __name__ == '__main__':
    main()
