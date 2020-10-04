package org.beetl.sql.core.mapping.type;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;

public class LocalDateTimeTypeHandler extends JavaSqlTypeHandler {
    @Override
    public Object getValue(ReadTypeParameter typePara) throws SQLException {
        Timestamp ts = typePara.getRs().getTimestamp(typePara.getIndex());
        return ts==null?null:ts.toLocalDateTime();
    }
    @Override
    public void setParameter(WriteTypeParameter writeTypeParameter,Object obj)throws SQLException {
        LocalDateTime ts = (LocalDateTime)obj;
        Timestamp time = Timestamp.valueOf(ts);
        writeTypeParameter.getPs().setTimestamp(writeTypeParameter.getIndex(),time);
    }
}
