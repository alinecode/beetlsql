package org.beetl.sql.core.mapping.type;

import java.sql.SQLException;

/**
 * 基础类，用来sql到java，java到sql的映射
 *
 * @author xiandafu
 */
public abstract class JavaSqlTypeHandler {
    public abstract Object getValue(TypeParameter typePara) throws SQLException;
}
