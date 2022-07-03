package org.beetl.sql.core.mapping.type;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.time.LocalDate;
import java.time.ZoneId;

/**
 * TODO,需要验证
 * @author xiandafu
 */
public class LocalDateTypeHandler extends JavaSqlTypeHandler {
    @Override
    public Object getValue(ReadTypeParameter typePara) throws SQLException {
        Timestamp ts = typePara.getRs().getTimestamp(typePara.getIndex());
        return ts==null?null:ts.toLocalDateTime().toLocalDate();
    }
    @Override
    public void setParameter(WriteTypeParameter writeTypeParameter,Object obj)throws SQLException {
        LocalDate localDate = (LocalDate)obj;
        long ts = localDate.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli();
        Timestamp time = new Timestamp(ts);
        writeTypeParameter.getPs().setTimestamp(writeTypeParameter.getIndex(),time);
    }

	@Override
	public int jdbcType() {
		return Types.DATE;
	}
}
