package com.harmony.componentgen;

/**
 * ArkTS 组件代码模板工厂。
 * 模板参照鸿蒙官方文档中 V1（@Component + @State）与
 * V2（@ComponentV2 + @Local）两种状态管理范式的标准写法。
 */
public final class CodeTemplateFactory {

    private CodeTemplateFactory() {
    }

    public static String build(TemplateOptions o) {
        StringBuilder sb = new StringBuilder();
        if (o.entry()) {
            sb.append("@Entry\n");
        }
        sb.append(o.v2() ? "@ComponentV2\n" : "@Component\n");
        sb.append("struct ").append(o.name()).append(" {");

        String stateDecl = null;
        String stateRef = null;
        if (o.withState()) {
            stateDecl = o.v2()
                    ? "@Local message: string = 'Hello World';"
                    : "@State message: string = 'Hello World';";
            stateRef = "this.message";
        }

        if (stateDecl != null) {
            sb.append("\n\n  ").append(stateDecl).append('\n');
        }

        sb.append("\n  build() {\n");
        if (o.entry()) {
            appendPageBody(sb, stateRef);
        } else {
            appendCustomComponentBody(sb, o.name(), stateRef);
        }
        sb.append("  }\n");
        sb.append("}\n");
        return sb.toString();
    }

    /**
     * 页面模板：RelativeContainer + 居中 Text，与 DevEco 新建 Page 模板一致。
     */
    private static void appendPageBody(StringBuilder sb, String stateRef) {
        sb.append("    RelativeContainer() {\n");
        if (stateRef != null) {
            sb.append("      Text(").append(stateRef).append(")\n");
        } else {
            sb.append("      Text('").append("Hello World").append("')\n");
        }
        sb.append("        .id('HelloWorld')\n")
          .append("        .fontSize($r('app.float.page_text_font_size'))\n")
          .append("        .fontWeight(FontWeight.Bold)\n")
          .append("        .alignRules({\n")
          .append("          center: { anchor: '__container__', align: VerticalAlign.Center },\n")
          .append("          middle: { anchor: '__container__', align: HorizontalAlign.Center }\n")
          .append("        })\n")
          .append("        .onClick(() => {\n")
          .append("        })\n")
          .append("    }\n")
          .append("    .height('100%')\n")
          .append("    .width('100%')\n");
    }

    /**
     * 自定义组件模板：Column 根容器。
     */
    private static void appendCustomComponentBody(StringBuilder sb, String name, String stateRef) {
        sb.append("    Column() {\n");
        if (stateRef != null) {
            sb.append("      Text(").append(stateRef).append(")\n");
        } else {
            sb.append("      Text('").append(name).append("')\n");
        }
        sb.append("        .fontSize(16)\n")
          .append("        .fontWeight(FontWeight.Medium)\n")
          .append("    }\n")
          .append("    .height('100%')\n")
          .append("    .width('100%')\n");
    }
}
