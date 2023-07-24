package org.beetl.sql.core.engine.template;

import org.beetl.core.NativeSecurityManager;

/**
 * 设置sql模板安全管理器，通常只有在“在线sql引擎”情况下，防止用户在sql里随意输入，比如"@java.lang.Thread.sleep(1000)"
 */
public class BeetlSQLTemplateSecurityManager implements NativeSecurityManager {
	@Override
	public boolean permit(Object resourceId, Class c, Object target, String method) {
		if (c.isArray()) {
			// 允许调用，但实际上会在在其后调用中报错。不归此处管理
			return true;
		}
		String name = c.getName();
		String className = null;
		String pkgName = null;
		int i = name.lastIndexOf('.');
		if (i != -1) {
			pkgName = name.substring(0, i);
			className = name.substring(i + 1);

		} else {
			// 无包名，允许调用
			return true;
		}

		if (pkgName.startsWith("java.lang")) {
			return !className.equals("Runtime")
					&& !className.equals("Process")
					&& !className.equals("ProcessBuilder")
					&& !className.equals("Thread") // https://gitee.com/xiandafu/beetl/issues/I6RUIP
					&& !className.equals("Class") //https://gitee.com/xiandafu/beetl/issues/I6RUIP#note_17223442
					&& !className.equals("System");
		}

		if(pkgName.startsWith("org.beetl.core")){
			//https://gitee.com/xiandafu/beetl/issues/I6RUIP
			return false;
		}

		if(pkgName.startsWith("javax.")){
			return false;
		}

		if(pkgName.startsWith("sun.")){
			return false;
		}

		return true;
	}
}
