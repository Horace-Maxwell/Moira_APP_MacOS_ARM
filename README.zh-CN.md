# Moira for macOS Apple Silicon

面向 Apple Silicon Mac 的 Moira 桌面发行版，提供签名、公证后的安装包，以及兼容 Java 11 的源码构建流程。最新 Release 里也附带 `Moira-jre.exe`，方便 Windows 用户直接找到现成安装包。

[最新发布](https://github.com/Horace-Maxwell/Moira_APP_MacOS_ARM/releases/latest) |
[English](./README.en.md) |
[项目首页](./README.md)

## 项目说明

这个仓库的目标很明确：

- 让 Moira 在 Apple Silicon Mac 上可以像正常桌面软件一样下载安装
- 让开发者继续用 Java 11 级别的源码方式编译、调试、维护
- 把构建、打包、签名、公证、发布流程整理成可重复执行的脚本

![Moira 在 Apple Silicon macOS 上的界面](./docs/assets/moira-app.png)

最终交付不是单纯“能在 IDEA 里跑起来”，而是一个可以分发给其他 M 系列 Mac 用户直接使用的 `Moira.app` / `.dmg`。

## 为什么会有这个仓库

[tutorial0/moira_macOS](https://github.com/tutorial0/moira_macOS) 已经把这份历史源码整理成了一个可以在 Apple Silicon Mac 上通过 IntelliJ IDEA + JDK 11 打开、运行、调试的工程，这是非常重要的一步。

本仓库是在这项工作的基础上继续往前推进，重点补齐：

- 真正可分发的 macOS 桌面打包流程
- 对签名 App 更安全的运行时读写路径
- 修复现代 macOS 上 SWT 的崩溃绘图路径
- Retina / HiDPI 模式下的图盘清晰度
- 右上角时间地点输入面板的可读性和布局
- GitHub Releases 下载分发

## 这个仓库已经做了哪些关键修复

### 1. 角距标注线功能恢复

原来的 `GC.setXORMode(true)` 即时擦写路径在 macOS 上会触发 SWT 崩溃。现在已经改成 paint 驱动的覆盖层重绘方式，功能恢复，同时避开了崩溃路径。

### 2. 高解析度 UI 清晰度修复

高解析度模式下，图盘不再依赖低分辨率缓存位图被系统放大，而是按屏幕缩放级别走 HiDPI 绘制路径，Retina 屏下更清楚。

### 3. 右上角输入控件 UI 整理

右上角日期、时间、地点输入区已经改成不透明深色面板，并提升了前景对比度；高解析度模式下图盘也会为这块面板预留空间，避免和右侧文本互相压住。

### 4. 运行时资源与用户数据分离

程序现在区分：

- App 内只读资源
- 用户可写数据目录 `~/Library/Application Support/Moira`

这样签名后的 `.app` 不需要再尝试往自己包内写入内容，缺失星历等运行时数据也会进入用户目录。

### 5. 构建与发布脚本补齐

仓库已经内置：

- `scripts/build-dev.sh`
- `scripts/package-macos.sh`
- `scripts/make-icns.sh`

可以直接完成开发构建、启动、App 打包、图标转换、签名、公证和发布资产生成。

## 面向普通用户的安装方式

### macOS

1. 打开 [最新发布页](https://github.com/Horace-Maxwell/Moira_APP_MacOS_ARM/releases/latest)
2. 下载 `Moira-*.dmg`
3. 打开镜像，把 `Moira.app` 拖到 `Applications`
4. 像普通 macOS 软件一样启动

如果你更喜欢直接下载 App 压缩包，Release 里也会附带一个 zip 版 `.app`。

### Windows

1. 打开 [最新发布页](https://github.com/Horace-Maxwell/Moira_APP_MacOS_ARM/releases/latest)
2. 下载 `Moira-jre.exe`
3. 在 Windows 中运行安装包

这样 Release 页面里会同时提供 macOS 的 `.dmg` / `.app.zip` 和 Windows 的 `Moira-jre.exe`，按系统直接选就可以。

## 面向开发者的源码构建

环境要求：

- Apple Silicon Mac
- 本地安装 JDK 11 或更高版本

编译：

```bash
./scripts/build-dev.sh
```

编译并启动：

```bash
./scripts/build-dev.sh --run
```

开发启动器位置：

```text
build/dev/Moira.sh
```

## 打包 macOS App

本地未签名打包：

```bash
./scripts/package-macos.sh
```

输出：

- `dist/Moira.app`
- `dist/Moira-1.50.0.dmg`

## 签名与公证

如果已经准备好 Apple Developer 证书与 notarytool profile：

```bash
export MOIRA_SIGN_IDENTITY="Developer ID Application: Your Name (TEAMID)"
export MOIRA_NOTARY_PROFILE="your-notary-profile"
./scripts/package-macos.sh
```

也可以使用 Apple ID + app-specific password：

```bash
export MOIRA_SIGN_IDENTITY="Developer ID Application: Your Name (TEAMID)"
export MOIRA_APPLE_ID="your@appleid.com"
export MOIRA_APPLE_APP_PASSWORD="xxxx-xxxx-xxxx-xxxx"
export MOIRA_APPLE_TEAM_ID="TEAMID"
./scripts/package-macos.sh
```

## 运行时路径模型

安装根目录解析顺序：

1. 启动器显式传入的安装目录
2. 打包后 `.app` 内的 `Contents/app`
3. 代码或运行目录
4. 当前工作目录兜底

用户可写目录固定为：

```text
~/Library/Application Support/Moira
```

星历下载和其他运行时可变文件都会进入这里。

## 致谢

- 原始桌面程序由 At Home Projects 发布
- Apple Silicon 源码整理、IDEA 可运行环境和早期适配基础来自 [tutorial0/moira_macOS](https://github.com/tutorial0/moira_macOS)

这里特别感谢 `tutorial0/moira_macOS` 的维护者。这个仓库保留了 Apple Silicon 下第一版真正可运行、可调试的工程基础，也让后续的打包、修复和发布工作有了可靠起点。

## 许可证

根目录主许可证保留为 [`LICENSE`](./LICENSE)，沿用历史 Moira 的 GPL 文本。第三方许可证说明单独整理在 [docs/licenses/LGPL-2.1.txt](./docs/licenses/LGPL-2.1.txt) 和 [docs/licenses/Swiss-Ephemeris-SEPL-0.2.txt](./docs/licenses/Swiss-Ephemeris-SEPL-0.2.txt)。

## 当前定位

这个仓库的定位是：给现代 Apple Silicon macOS 提供一个稳定、可维护、可下载分发的 Moira 桌面版本。
