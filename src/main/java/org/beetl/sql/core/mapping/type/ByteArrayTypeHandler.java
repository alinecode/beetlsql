package org.beetl.sql.core.mapping.type;

import java.sql.SQLException;

public class ByteArrayTypeHandler extends JavaSqlTypeHandler {

    @Override
    public Object getValue(TypeParameter typePara) throws SQLException {
        return typePara.rs.getBytes(typePara.index);
    }

}
