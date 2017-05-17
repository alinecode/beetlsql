package org.beetl.sql.core.mapping.type;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ShortTypeHandler extends JavaSqlTypeHandler implements PrimitiveValue {

    Short defaultValue = 0;

    @Override
    public Object getValue(TypeParameter typePara) throws SQLException {
        ResultSet rs = typePara.rs;
        short a = rs.getShort(typePara.index);
        if (rs.wasNull()) {
            if (typePara.isPrimitive()) {
                return defaultValue;
            } else {
                return null;
            }
        } else {
            return a;
        }

    }

    @Override
    public Object getDefaultValue() {
        return defaultValue;
    }

}
