package org.beetl.sql.core.mapping.type;

import java.io.Reader;
import java.sql.Clob;
import java.sql.NClob;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

import org.beetl.sql.core.kit.LobKit;

public class StringTypeHandler extends JavaSqlTypeHandler {

    @Override
    public Object getValue(TypeParameter typePara) throws SQLException {
        ResultSet rs = typePara.rs;
        int index = typePara.index;
        
        if (typePara.dbName.equals("oracle")) {
            int type = typePara.meta.getColumnType(index);
            switch (type) {
                case java.sql.Types.CLOB: {
                		Clob clob = rs.getClob(index);
                		if(clob==null){
                			return null;
                		}
                    Reader r = clob.getCharacterStream();
                    return LobKit.getString(r);
                }
                case Types.NCLOB: {
                		NClob nclob =rs.getNClob(index);
                		if(nclob==null){
                			return null;
                		}
                    Reader r = nclob.getCharacterStream();
                    return LobKit.getString(r);
                }


                default:
                    return rs.getString(index);

            }
        } else {
            //认为其他数据库都支持直接通过jdbc获取字符串，如果不是这样，需要扩展StringTypeHandler
            return rs.getString(index);
        }

    }

}
