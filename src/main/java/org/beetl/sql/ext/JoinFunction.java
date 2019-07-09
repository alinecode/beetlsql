package org.beetl.sql.ext;

import org.beetl.core.Context;
import org.beetl.core.Function;
import org.beetl.core.GeneralLoopStatus;
import org.beetl.core.ILoopStatus;
import org.beetl.sql.core.engine.SQLParameter;
import org.beetl.sql.core.kit.BeanKit;

import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author "Sean-[重庆]" 284771807@qq.com; xiandafu 
 *
 */
public class JoinFunction implements Function {

	private static String join(ILoopStatus it, List dbParas) {

		StringBuilder buf = new StringBuilder();
		while (it.hasNext()) {
			Object o = it.next();
			if (!it.isFirst()) {
				buf.append(',');
			}
			buf.append('?');
			dbParas.add(new SQLParameter(null, o));

		}
		return buf.toString();
	}

	public static void main(String[] args) {
		int[] a = {1, 2};
		Object obj = a;
		Class c = obj.getClass();
		if (c.isArray()) {
			Object[] cs = (Object[]) obj;
			System.out.println(cs.length);
		}
	}

	public Object call(Object[] paras, Context ctx) {
		Object temp = paras[0];
		if (temp == null) {
			throw new NullPointerException("join 参数为null");
		}

		ILoopStatus it = GeneralLoopStatus.getIteratorStatus(temp);
		if (it == null) {
			throw new NullPointerException("join 参数为必须为集合，数组，Iterator");
		}
		if (paras.length == 2) {
			//获取对象属性join(objs,"name");
			it = getValue(it, (String) paras[1]);
		}

		List<SQLParameter> dbParas = (List<SQLParameter>) ctx.getGlobal("_paras");

		try {
			ctx.byteWriter.writeString(join(it, dbParas));
		} catch (Exception e) {
			// IO错误这里不抛出
		}
		return null;
	}

	private ILoopStatus getValue(ILoopStatus it, String attrName) {
		LinkedList list = new LinkedList();
		while (it.hasNext()) {
			list.add(BeanKit.getBeanProperty(it.next(), attrName));
		}

		return GeneralLoopStatus.getIteratorStatus(list);
	}
}