package org.beetl.sql.core.mapping.type;

import java.sql.SQLException;

public class SqlDateTypeHandler extends JavaSqlTypeHandler {

    @Override
    public Object getValue(TypeParameter typePara) throws SQLException {
        java.sql.Date a = typePara.rs.getDate(typePara.index);
        return a;

    }

}
