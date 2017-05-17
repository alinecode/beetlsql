package org.beetl.sql.core.mapping.type;

import java.math.BigDecimal;
import java.sql.SQLException;

public class BigDecimalTypeHandler extends JavaSqlTypeHandler {

    @Override
    public Object getValue(TypeParameter typePara) throws SQLException {
        BigDecimal a = typePara.rs.getBigDecimal(typePara.index);
        return a;
    }

}
