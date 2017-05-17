package org.beetl.sql.core.mapping.type;

import java.sql.SQLException;

public class TimestampTypeHandler extends JavaSqlTypeHandler {

    @Override
    public Object getValue(TypeParameter typePara) throws SQLException {
        return typePara.rs.getTimestamp(typePara.index);

    }

}
