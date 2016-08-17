package org.beetl.sql.core.orm;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.beetl.core.Context;
import org.beetl.core.Function;
/**
 * 记录映射关系
 * <pre>
 * 
 * db.mappingClass.single({"id":"orderId"},"com.test.OrderDetail","orderDetail");
 * db.mappingSql.many({"id":"orderId"},"orderDetail.query","orderDetail");
 * </pre>
 * @author xiandafu
 *
 */
public class ORMSingleEntityFunction extends MappingFunctionHelper implements Function {

	@Override
	public Object call(Object[] paras, Context ctx) {
		this.parse(false, true, paras, ctx);
		return null;
	}

}
