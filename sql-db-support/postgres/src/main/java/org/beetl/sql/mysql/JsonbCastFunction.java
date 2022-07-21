package org.beetl.sql.mysql;

import org.beetl.core.Context;
import org.beetl.core.Function;
import org.beetl.sql.core.engine.SQLParameter;

import java.io.IOException;
import java.sql.Types;
import java.util.List;

public class JsonbCastFunction implements Function {
	@Override
	public Object call(Object[] objects, Context context) {
		Object value = objects[0];
		List list = (List) context.getGlobal("_paras");
		SQLParameter sqlPara = new SQLParameter(value);
		//json格式
		sqlPara.setJdbcType(Types.OTHER);
		list.add(sqlPara);
		return "?::JSON";

	}
}
