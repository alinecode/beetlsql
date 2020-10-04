package org.beetl.sql.core.loader;

import org.beetl.sql.clazz.kit.Plugin;
import org.beetl.sql.core.SQLSource;

import java.io.IOException;

/**
 * 解析sql文件，取得sql内容,目前内置的是markdown格式的的sql文件
 */
@Plugin
public interface SQLFileParser {
    /**
     *
     * @return 如果为null，表示读取结束
     * @throws IOException
     */
    public SQLSource next() throws IOException ;
}
