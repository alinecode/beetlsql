package org.beetl.sql.core.engine;

import org.beetl.core.Context;
import org.beetl.core.Function;

/**
 * 用于insertTemlate，参考AbstractDBStyle.appendInsertTemplateValue
 * @author xiandafu
 *
 */
public class TestColNullFunction implements Function {

	@Override
	public String call(Object[] paras, Context ctx) {
		Object var = paras[0];
		String col = (String) paras[1];
		if (var == null) {
			return "";
		} else {
			return ","+col ;
		}

	}

}
