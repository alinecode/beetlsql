/**
 * BeetlSQL代码生成框架，BeetlSQL提供TableDesc和ClassDesc 供生成代码
 * <ul>
 *     <li>@{link SourceBuilder} 生成特定代码，如POJO，SQL语句，数据库文档片段</li>
 *     <li>@{link SourceConfig} 包含多个SourceBuilder，用来生成一系列代码</li>
 *     <li>@{link SourceFilter},当调用@{link SourceConfig#genAll}的时候，可以过滤不需要生成的表</li>
 *     <li>@{link BaseProject},生成的目标，比如生成直接输出到控制台，或者生成到当前项目工程里，或者生成存放在字符串里</li>
 * </ul>
 *
 *
 */
package org.beetl.sql.gen;