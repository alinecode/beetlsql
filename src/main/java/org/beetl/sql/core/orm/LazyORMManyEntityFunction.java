package org.beetl.sql.core.orm;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.beetl.core.Context;
import org.beetl.core.Function;
/**
 * 记录映射关系,可以处理一对多，多对对多关系
 * <pre>
 * 
 * orm.many({"id":"orderId"},"OrderDetail");
 * orm.single({"id":"orderId"},"orderDetail.query","OrderDetail");
 * orm.many({"id":"user_id"},"role.selectRoleByUserId","Role");
 * </pre>
 * @author xiandafu
 *
 */
public class LazyORMManyEntityFunction extends MappingFunctionHelper implements Function {

	@Override
	public Object call(Object[] paras, Context ctx) {
		this.parse(false, true,paras, ctx);
		return null;
	}

}
