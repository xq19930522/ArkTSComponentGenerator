# -*- coding: utf-8 -*-
"""构建 ArkTS Component Generator 插件：
1. 用 DevEco Studio 自带 JBR javac 编译（classpath 指向 DevEco 平台 jar）
2. 打成插件 jar（含 META-INF/plugin.xml 与图标）
3. 打成可 Install-from-Disk 的 zip 分发包
"""
import os
import shutil
import subprocess
import sys
import zipfile

ROOT = os.path.dirname(os.path.abspath(__file__))
DEVECO = os.environ.get("DEVECO_HOME", r"E:\DevEco Studio")
JAVAC = os.path.join(DEVECO, "jbr", "bin", "javac.exe")
OUT = os.path.join(ROOT, "out")
CLASSES = os.path.join(OUT, "classes")
JAR_PATH = os.path.join(OUT, "ComponentGenerator.jar")
DIST = os.path.join(ROOT, "dist")
ZIP_PATH = os.path.join(DIST, "ArkTSComponentGenerator-1.0.0.zip")


def find_sources():
    src = os.path.join(ROOT, "src")
    result = []
    for base, _, files in os.walk(src):
        for f in files:
            if f.endswith(".java"):
                result.append(os.path.join(base, f))
    return result


def main():
    classpath = os.pathsep.join(
        os.path.join(DEVECO, "lib", name) for name in [
            "app.jar", "util.jar", "util_rt.jar", "util-8.jar", "annotations.jar",
        ]
    )

    for d in (CLASSES, DIST):
        shutil.rmtree(d, ignore_errors=True)
        os.makedirs(d, exist_ok=True)

    sources = find_sources()
    print(f"[1/3] 编译 {len(sources)} 个 Java 文件 ...")
    cmd = [JAVAC, "-encoding", "UTF-8", "-nowarn",
           "-cp", classpath, "-d", CLASSES] + sources
    proc = subprocess.run(cmd, capture_output=True, text=True)
    if proc.returncode != 0:
        print(proc.stdout)
        print(proc.stderr)
        sys.exit("编译失败")
    print("      编译成功")

    print("[2/3] 打包插件 jar ...")
    if os.path.exists(JAR_PATH):
        os.remove(JAR_PATH)
    with zipfile.ZipFile(JAR_PATH, "w", zipfile.ZIP_DEFLATED) as zf:
        # manifest
        zf.writestr("META-INF/MANIFEST.MF", "Manifest-Version: 1.0\n\n")
        for base, _, files in os.walk(CLASSES):
            for f in files:
                full = os.path.join(base, f)
                arc = os.path.relpath(full, CLASSES).replace(os.sep, "/")
                zf.write(full, arc)
        res = os.path.join(ROOT, "resources")
        for base, _, files in os.walk(res):
            for f in files:
                full = os.path.join(base, f)
                arc = os.path.relpath(full, res).replace(os.sep, "/")
                zf.write(full, arc)
    print(f"      {JAR_PATH}")

    print("[3/3] 制作分发 zip ...")
    if os.path.exists(ZIP_PATH):
        os.remove(ZIP_PATH)
    with zipfile.ZipFile(ZIP_PATH, "w", zipfile.ZIP_DEFLATED) as zf:
        zf.write(JAR_PATH, "ArkTSComponentGenerator/lib/ComponentGenerator.jar")
    print(f"      {ZIP_PATH}")
    print("完成。")


if __name__ == "__main__":
    main()
