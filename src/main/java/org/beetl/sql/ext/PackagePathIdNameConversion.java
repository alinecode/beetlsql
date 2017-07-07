package org.beetl.sql.ext;

import java.lang.reflect.Method;

import org.beetl.sql.core.SQLIdNameConversion;
import org.beetl.sql.core.kit.StringKit;
/**
 * sqlId 命名转化，将sqlId转化到对应的sqlroot目录下
 * @author xiandafu
 *
 */
public class PackagePathIdNameConversion implements SQLIdNameConversion {

	@Override
	public String getId(Class z, Method m) {
		// 有可能没有包名字，谁这么搞呢？
		String pkg = z.getPackage().getName();
		String cls = StringKit.toLowerCaseFirstOne(z.getSimpleName());
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
