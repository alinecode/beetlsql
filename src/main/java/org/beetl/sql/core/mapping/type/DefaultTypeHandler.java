package org.beetl.sql.core.mapping.type;

import java.sql.SQLException;

public class DefaultTypeHandler extends JavaSqlTypeHandler {

    @Override
    public Object getValue(TypeParameter typePara) throws SQLException {
        return typePara.rs.getObject(typePara.index);
    }

}
