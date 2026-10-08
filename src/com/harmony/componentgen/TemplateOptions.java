package com.harmony.componentgen;

/**
 * 代码生成选项。
 *
 * @param name      组件 / 结构体名称（PascalCase）
 * @param v2        true = @ComponentV2（状态管理 V2），false = @Component（V1）
 * @param entry     true = 生成 @Entry 页面入口
 * @param withState true = 携带示例状态变量（V1: @State / V2: @Local）
 */
public record TemplateOptions(String name, boolean v2, boolean entry, boolean withState) {
}
