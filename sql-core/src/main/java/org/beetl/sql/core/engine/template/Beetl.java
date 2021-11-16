package org.beetl.sql.core.engine.template;

import org.beetl.core.AntlrProgramBuilder;
import org.beetl.core.Configuration;
import org.beetl.core.GroupTemplate;
import org.beetl.sql.clazz.kit.StringKit;
import org.beetl.sql.core.engine.StringSqlTemplateLoader;
import org.beetl.sql.core.loader.SQLLoader;

import java.nio.charset.Charset;
import java.util.Properties;

public class Beetl {
	GroupTemplate gt = null;
	Properties ps = null;

	public Beetl(SQLLoader loader, Properties ps) {
		try {
			this.ps = ps;

			StringSqlTemplateLoader resourceLoader = new StringSqlTemplateLoader(loader);
			Configuration cfg = new Configuration(ps);
			gt = new GroupTemplate(resourceLoader, cfg);
			//会被SQLManagerBuilder的setProduct覆盖

			String charset = ps.getProperty("CHARSET");
			if (StringKit.isBlank(charset)) {
				charset = Charset.defaultCharset().name();

			}

			//对isBlank参数增加安全输出控制，如果不存在在，为空，返回true
			AntlrProgramBuilder.safeParameters.add("isBlank");

		} catch (Exception ex) {
			throw new IllegalStateException(ex);
		}

	}




	public GroupTemplate getGroupTemplate() {
		return gt;
	}

	public Properties getPs() {
		return ps;
	}


}
