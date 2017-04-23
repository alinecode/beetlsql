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
public class TestColNullFunction implements Function {

	@Override
	public String call(Object[] paras, Context ctx) {
		Object arg = paras[0];
		String paraName = (String)paras[1];
		if(arg==null){
			return "";
		}else{
			return paraName+",";
		}
		
	}

}
