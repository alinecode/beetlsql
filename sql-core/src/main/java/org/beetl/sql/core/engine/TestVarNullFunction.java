package org.beetl.sql.core.engine;

import org.beetl.core.Context;
import org.beetl.core.Function;

import java.io.IOException;
import java.util.List;

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
			try{
				ctx.byteWriter.writeString("?");
			} catch (IOException e) {
				//不可能发声
				throw new RuntimeException(e);
			}
			List list = (List) ctx.getGlobal("_paras");
			list.add(new SQLParameter(var));
			return "";
		} else {
			return defaultValue ;
		}


	}

}
