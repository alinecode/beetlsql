package org.beetl.sql.core.mapping.type;

import java.sql.ResultSet;
import java.sql.SQLException;

public class IntegerTypeHandler extends JavaSqlTypeHandler implements PrimitiveValue {

    static Integer defaultValue = 0;

    @Override
    public Object getValue(TypeParameter typePara) throws SQLException {
        ResultSet rs = typePara.rs;
        int a = rs.getInt(typePara.index);
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
        // TODO Auto-generated method stub
        return defaultValue;
    }

}
