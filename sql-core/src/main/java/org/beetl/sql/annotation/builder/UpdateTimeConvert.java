package org.beetl.sql.annotation.builder;

import org.beetl.sql.clazz.kit.BeanKit;
import org.beetl.sql.core.ExecuteContext;

import java.util.Date;

/**
 * 一个持久化前更新字段，使用当前日期格式
 * @see UpdateTime
 * @author xiandafu
 */
public class UpdateTimeConvert implements AttributeConvert {
    @Override
    public  Object toDb(ExecuteContext ctx,  Class cls,String name, Object dbValue){
		if(ctx.isUpdate){
			Date now = new Date();
			BeanKit.setBeanProperty(dbValue,now,name);
			return now;
		}

		return null;

    }

}
