package org.beetl.sql.core.engine;

import org.beetl.core.Context;
import org.beetl.core.Function;

/**
 * 用于insertTemlate，参考AbstractDBStyle.appendInsertTemplateValue
 * @author xiandafu
 *
 */
public class TestVarNullFunction implements Function {

	@Override
	public Object call(Object[] paras, Context ctx) {
		Object var = paras[0];
		String defaultValue = (String) paras[1];
		if (var != null) {
			return var+",";
		} else {
			return defaultValue + ",";
		}

	}

}
