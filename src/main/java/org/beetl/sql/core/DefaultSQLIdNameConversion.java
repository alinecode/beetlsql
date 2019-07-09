package org.beetl.sql.core;

import org.beetl.sql.core.kit.StringKit;

import java.lang.reflect.Method;
/**
 * 类名首字母小写＋ 方法名 成为mapper对应的sqlId
 * 
 * sqlId 以 "." 区分最后一部分是文件名字，前面转为为文件相对路径
 * @author xiandafu
 *
 */
public class DefaultSQLIdNameConversion implements SQLIdNameConversion {

	@Override
	public String getId(Class mapper,Class entity, Method m) {
		// TODO Auto-generated method stub
		String ns = StringKit.toLowerCaseFirstOne(entity.getSimpleName());
		String methodName = m.getName();
		return ns+"."+methodName;
	}

	@Override
	public String getPath(String sqlId) {
		String modelName = sqlId.substring(0, sqlId.lastIndexOf(".") );
        String path  = modelName.replace('.', '/');
        return path;
	}

}
