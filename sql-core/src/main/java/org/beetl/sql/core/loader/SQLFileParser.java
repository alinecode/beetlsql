package org.beetl.sql.core.loader;

import org.beetl.sql.clazz.kit.Plugin;
import org.beetl.sql.core.SQLSource;

import java.io.IOException;

/**
 * 解析sql文件，取得sql内容,目前内置的是markdown格式的的sql文件
 * @author xiandafu
 */
@Plugin
public interface SQLFileParser {
    /**
     * 解析文件
     * @return 如果为null，表示读取结束
     * @throws IOException
     */
    SQLSource next() throws IOException ;
}
