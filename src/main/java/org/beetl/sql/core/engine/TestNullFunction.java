package org.beetl.sql.core.engine;

import java.io.IOException;
import java.util.List;

import org.beetl.core.Context;
import org.beetl.core.Function;
/**
 * 用于insertTemlate，参考AbstractDBStyle.appendInsertTemplateValue
 * @author xiandafu
 *
 */
public class TestNullFunction implements Function {

	@Override
	public Object call(Object[] paras, Context ctx) {
		Object arg = paras[0];
		String paraName = (String)paras[1];
		if(arg==null){
			return "";
		}
		try {
			//多余的逗号将被trim去掉
			ctx.byteWriter.writeString("?,");
			List list = (List)ctx.getGlobal("_paras");
			list.add(new SQLParameter(paraName,arg));
			return "";
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "";
		}
		
	}

}
