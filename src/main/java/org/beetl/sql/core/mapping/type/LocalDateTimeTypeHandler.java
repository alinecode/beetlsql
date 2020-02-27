package org.beetl.sql.core.mapping.type;

import java.sql.SQLException;
import java.sql.Timestamp;

public class LocalDateTimeTypeHandler extends JavaSqlTypeHandler {
    @Override
    public Object getValue(TypeParameter typePara) throws SQLException {
        Timestamp ts = typePara.getRs().getTimestamp(typePara.getIndex());
        return ts==null?null:ts.toLocalDateTime();
    }
}
