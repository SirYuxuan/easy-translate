# Easy Translate Plugin

一个简单易用的 IntelliJ IDEA 翻译插件，支持多种翻译引擎和自定义词典功能。

## 功能特点

- 支持多种翻译引擎：
  - 百度翻译
  - 有道翻译
  - 谷歌翻译

- 快速翻译：
  - 选中文本后按快捷键（默认 Alt + T）即可翻译
  - 自动检测中英文并进行互译
  - 支持自定义快捷键

- 自定义词典：
  - 支持添加自定义翻译对照
  - 可设置是否区分大小写
  - 可单独启用/禁用词典条目

- 文本转换功能：
  - 大小写转换
  - 驼峰命名转换
  - 下划线命名转换
  - 常量命名转换

## 安装要求

- IntelliJ IDEA 2023.2 或更高版本
- Java 17 或更高版本

## 支持的 IDE

- IntelliJ IDEA (Community & Ultimate)
- PyCharm (Community & Professional)
- WebStorm
- PhpStorm
- GoLand
- CLion
- DataGrip
- RubyMine
- Rider

## 安装方法

1. 在 IDE 中打开 Settings/Preferences
2. 选择 Plugins
3. 点击 Marketplace
4. 搜索 "Easy Translate"
5. 点击 Install 安装

## 使用方法

### 基本翻译
1. 选中需要翻译的文本
2. 使用快捷键 Alt + T（可自定义）或右键菜单中的"快速翻译"
3. 文本将自动翻译并替换选中内容

### 配置翻译引擎
1. 打开 Settings/Preferences
2. 选择 Tools -> 翻译助手
3. 选择翻译引擎并配置相应的 API 密钥：
   - 百度翻译：需要 APP ID 和密钥
   - 有道翻译：需要应用 ID 和密钥
   - 谷歌翻译：可选配置代理服务器

### 自定义词典
1. 打开 Settings/Preferences
2. 选择 Tools -> 翻译助手
3. 在词典设置中添加、编辑或删除翻译对照
4. 可以设置是否区分大小写和是否启用

### 文本转换
1. 选中需要转换的文本
2. 使用快捷键 Alt + Shift + T 或右键菜单中的"文本转换"
3. 选择需要的转换方式：
   - 转大写
   - 转小写
   - 首字母大写
   - 转驼峰
   - 转下划线

## 开发说明

### 环境要求
- Java Development Kit (JDK) 17
- Gradle 8.4

### 构建项目
```bash
./gradlew clean build
```

### 运行测试
```bash
./gradlew test
```

### 本地调试
```bash
./gradlew runIde
```

## 许可证

本项目采用 Apache 2.0 许可证。

## 贡献指南

欢迎提交 Issue 和 Pull Request。在提交 PR 之前，请确保：

1. 代码符合项目的编码规范
2. 添加了必要的测试
3. 更新了相关文档
4. 所有测试通过

## 问题反馈

如果您在使用过程中遇到任何问题，或有任何建议，请：

1. 提交 Issue
2. 发送邮件至 dev@oofo.cc
3. 访问项目主页 https://www.oofo.cc 