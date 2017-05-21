package org.beetl.sql.core.mapping.type;

import java.sql.SQLException;
import java.sql.Timestamp;

public class DateTypeHandler extends JavaSqlTypeHandler {

    @Override
    public Object getValue(TypeParameter typePara) throws SQLException {
        Timestamp a = typePara.rs.getTimestamp(typePara.index);
        if (a != null) {
            return new java.util.Date(a.getTime());
        } else {
            return null;
        }
        

    }

}
