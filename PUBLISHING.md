# 发布到 JetBrains Marketplace 操作手册

## 已完成（本地）

| 产物 | 路径 |
| --- | --- |
| **签名后的上传包（传这个）** | `dist/ArkTSComponentGenerator-1.0.0-signed.zip` |
| 未签名原始包 | `dist/ArkTSComponentGenerator-1.0.0.zip` |

- 签名密钥对：`cert/private.pem`（私钥）、`cert/chain.crt`（证书链，10 年有效）
- 私钥密码：`cert/keypass.txt`
- ⚠️ **请把 `cert/` 整个目录备份到安全位置（私钥 + 密码），以后每次发版都要用它签名，丢失只能换新证书**

重新签名命令（以后发新版用）：

```bash
"E:\DevEco Studio\jbr\bin\java.exe" -jar tools/marketplace-zip-signer-cli.jar sign \
  -in "dist/ArkTSComponentGenerator-新版本.zip" \
  -out "dist/ArkTSComponentGenerator-新版本-signed.zip" \
  -cert-file cert/chain.crt -key-file cert/private.pem \
  -key-pass "$(cat cert/keypass.txt)"
```

---

## 需要你自己操作的步骤（约 10 分钟）

### 第 1 步：注册 JetBrains 账号

1. 打开 <https://account.jetbrains.com/signup> 注册账号（有 JetBrains 账号可直接登录）
2. 打开 <https://plugins.jetbrains.com> 用该账号登录

### 第 2 步：创建 Vendor（发布者资料）

1. 登录后点右上角头像 → **My Profile / Account Settings**
2. 找到 **Vendors / Organizations** → **Add new vendor**
3. 填写：Vendor 名称（如 `HarmonyOSSN`）、邮箱、网址（可填 GitHub 仓库地址或留空后续补）
   - ⚠️ 如果上架表单提示欧盟消费者保护法规要求，个人发布者需要勾选相应声明并填写联系邮箱

### 第 3 步：上传插件（首次必须手动上传）

1. 在 <https://plugins.jetbrains.com> 登录状态下，点头像 → **Upload plugin**（或 **My Plugins → Add new plugin**）
2. 上传文件选择：`F:\javaplugin\ComponentGenerator\dist\ArkTSComponentGenerator-1.0.0-signed.zip`
3. 表单各字段按下面"上架资料"填写（已为你备好，直接复制）

### 第 4 步：提交审核

- 填完点 **Add plugin / Submit for review**
- 首次上传进入审核，一般 **1~5 个工作日**
- 审核通过后即公开上架，IDEA 用户可在 Plugins 市场里搜索安装

---

## 上架资料（复制粘贴用）

**Plugin name**
```
ArkTS Component Generator
```

**Description**（HTML，Marketplace 支持）：

```html
Generate ArkTS UI component skeleton code for HarmonyOS / OpenHarmony development
in IntelliJ-based IDEs, inspired by the Flutter "New Widget" plugin.

<h3>Features</h3>
<ul>
  <li><b>State management V1</b>: generates <code>@Component</code> + <code>@State</code></li>
  <li><b>State management V2</b>: generates <code>@ComponentV2</code> + <code>@Local</code> (official V2 paradigm)</li>
  <li>Optional <code>@Entry</code> page template — RelativeContainer with a centered Text, identical to the DevEco Studio "New Page" template</li>
  <li>Optional sample state variable with <code>Text(this.message)</code> binding</li>
  <li>Invoke via Generate menu (Alt+Insert), editor right-click menu, or Ctrl+Alt+Shift+K</li>
  <li>Code is inserted at the caret, auto-aligned to the current line indentation</li>
  <li>Only enabled inside <code>.ets</code> files</li>
</ul>

<h3>Usage</h3>
<ol>
  <li>Open any <code>.ets</code> file and place the caret where you want the component</li>
  <li>Press Alt+Insert (or right-click → Generate) and choose <b>ArkTS Component...</b></li>
  <li>Enter the component name, pick V1/V2 mode, click OK</li>
</ol>

<p>Sample output (V1, @Entry page):</p>
<pre>
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
</pre>

<p>Compatible with DevEco Studio 6.1.x (platform build 243.24978+).
DevEco Studio users can also install the plugin from disk.</p>
```

**Tags**
```
arkts, harmonyos, openharmony, deveco, code generation, ui
```

**Category**
```
Code tools
```

**License**
```
Apache License 2.0
```

---

## 备注

- Marketplace 只面向 JetBrains 系 IDE；**DevEco Studio 用户无法从 Marketplace 安装**，
  鸿蒙开发者仍然需要把 `ArkTSComponentGenerator-1.0.0.zip` 用
  Settings → Plugins → ⚙ → Install Plugin from Disk 安装
- 首次上架后，以后发新版可以在 Marketplace 后台直接传新 zip，无需再等建店审核（版本更新审核更快）
- 若后续想接入 `publishPlugin` 自动发布（Gradle），随时可以把这个工程改成 Gradle 版
