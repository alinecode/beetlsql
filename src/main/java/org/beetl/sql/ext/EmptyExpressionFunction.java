package org.beetl.sql.ext;

import java.util.Collection;
import java.util.Map;

import org.beetl.core.Context;
import org.beetl.core.Function;
import org.beetl.core.misc.PrimitiveArrayUtil;

/**
 * 判断全局变量是否为“空”，下列情况属于为空·的情况
 * <ul>
 * 
 * <li>变量不存在</li>
 * <li>变量存在，但为null</li>
 * <li>变量存在，但是字符，其长途为0</li>
 * <li>变量存在，但是空集合</li>
 * <li>变量存在，但是空数组</li>
 * </ul>
 * 参数可以一个到多个,如<p>
 * ${empty("list")}
 * @author joelli
 *
 */
public class EmptyExpressionFunction implements Function
{

	public Boolean call(Object[] paras, Context ctx)
	{

		if (paras.length == 0)
			return true;
		Object result = paras[0];
		if (result == null)
			return true;
		if (result instanceof String)
		{

			if (((String) result).length() != 0)
			{
				return false;
			}

		}
		else if (result instanceof Collection)
		{
			if (((Collection) result).size() != 0)
			{
				return false;
			}
		}
		else if (result instanceof Map)
		{
			if (((Map) result).size() != 0)
			{
				return false;
			}
		}
		else if (result.getClass().isArray())
		{
			Class ct = result.getClass().getComponentType();
			if (ct.isPrimitive())
			{
				return PrimitiveArrayUtil.getSize(result)==0;
			}
			else
			{
				return ((Object[]) result).length==0;
			}
		}
		else
		{
			return false;
		}

		return true;

	}

}