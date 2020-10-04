package org.beetl.sql.gen;

import org.beetl.sql.core.meta.MetadataManager;

/**
 * 代码生成过滤器
 * @author xiandafu
 */
public interface SourceFilter {
    /**
     * 如果返回false，则不生成代码
     * @param metadataManager
     * @param tableName
     * @return
     */
    boolean accept(MetadataManager metadataManager, String tableName);
}
