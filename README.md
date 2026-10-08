# ArkTS Component Generator（DevEco Studio 插件）

在 DevEco Studio 中快速生成 ArkTS 组件骨架代码，交互方式参照 Flutter 的 "New Widget" 插件：
在 `.ets` 编辑器中通过 **Generate 菜单（Alt+Insert）** 或 **右键菜单** 唤起对话框，
填写组件名称、选择状态管理模式后，代码自动插入光标处。

## 功能

| 选项 | 说明 |
| --- | --- |
| Component name | 组件/结构体名称（自动首字母大写，校验合法 ArkTS 标识符） |
| @Component (V1) | 状态管理 V1：生成 `@Component` + `@State` |
| @ComponentV2 (V2) | 状态管理 V2：生成 `@ComponentV2` + `@Local`（鸿蒙官方 V2 范式） |
| 生成 @Entry 页面入口 | 勾选后生成 `@Entry` 页面，模板为 RelativeContainer + 居中 Text（与 DevEco 新建 Page 一致）；不勾选则生成 Column 根容器的自定义组件 |
| 携带示例状态变量 | 勾选后附带 `message` 状态字段与 `Text(this.message)` 绑定 |

快捷键：`Ctrl + Alt + Shift + K`

## 安装

1. 打开 DevEco Studio
2. **File → Settings → Plugins**（macOS：**DevEco Studio → Preferences → Plugins**）
3. 点击右上角 ⚙ 齿轮图标 → **Install Plugin from Disk...**
4. 选择 `dist/ArkTSComponentGenerator-1.0.0.zip`
5. 重启 DevEco Studio

## 使用

1. 打开任意 `.ets` 文件，将光标放到要插入代码的位置
2. 按 `Alt + Insert`（或右键 → Generate）选择 **ArkTS Component...**（或在编辑器右键菜单中选择）
3. 在弹窗中填写名称、选择 V1/V2 模式，点击 OK

V1 页面示例输出：

```typescript
@Entry
@Component
struct Index {
  @State message: string = 'Hello World';

  build() {
    RelativeContainer() {
      Text(this.message)
        .id('HelloWorld')
        .fontSize($r('app.float.page_text_font_size'))
        .fontWeight(FontWeight.Bold)
        .alignRules({
          center: { anchor: '__container__', align: VerticalAlign.Center },
          middle: { anchor: '__container__', align: HorizontalAlign.Center }
        })
        .onClick(() => {
        })
    }
    .height('100%')
    .width('100%')
  }
}
```

V2 页面示例输出：

```typescript
@Entry
@ComponentV2
struct Index {
  @Local message: string = 'Hello World';

  build() {
    RelativeContainer() {
      Text(this.message)
        ...
    }
    .height('100%')
    .width('100%')
  }
}
```

## 重新构建

```
python build.py
```

脚本使用 DevEco Studio 自带 JBR（`E:\DevEco Studio\jbr`）编译，
平台 classpath 取自 `E:\DevEco Studio\lib`。
若 DevEco Studio 安装路径不同，设置环境变量 `DEVECO_HOME` 后重新运行。

兼容版本：DevEco Studio 6.1.x（平台构建号 243.24978），`since-build=243`。

## 工程结构

```
src/com/harmony/componentgen/
├── GenerateComponentAction.java   # Action：注册 Generate/右键菜单，插入代码
├── ComponentGeneratorDialog.java  # 对话框：名称 + V1/V2 + @Entry + 状态变量
├── CodeTemplateFactory.java       # 模板工厂：V1/V2 × 页面/组件 × 有无状态
└── TemplateOptions.java           # 选项 record
resources/
├── META-INF/plugin.xml            # 插件清单
└── icons/pluginIcon.svg
build.py                           # 编译 + 打包脚本
dist/ArkTSComponentGenerator-1.0.0.zip   # 可安装分发包
```
