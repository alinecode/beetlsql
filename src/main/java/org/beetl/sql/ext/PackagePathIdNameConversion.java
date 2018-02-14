package org.beetl.sql.ext;

import org.beetl.sql.core.SQLIdNameConversion;
import org.beetl.sql.core.kit.BeanKit;
import org.beetl.sql.core.kit.StringKit;

import java.lang.reflect.Method;
/**
 * sqlId 命名转化，将sqlId转化到对应的sqlroot目录下
 * @author xiandafu
 *
 */
public class PackagePathIdNameConversion implements SQLIdNameConversion {

	@Override
	public String getId(Class mapper,Class entity, Method m) {
		// 有可能没有包名字，谁这么搞呢？
		String pkg = BeanKit.getPackageName(entity);
		String cls = StringKit.toLowerCaseFirstOne(entity.getSimpleName());
		String ns = pkg+"."+cls;
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
