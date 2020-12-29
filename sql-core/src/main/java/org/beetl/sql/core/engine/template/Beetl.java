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
			boolean product = Boolean.parseBoolean(ps.getProperty("PRODUCT_MODE"));
			StringSqlTemplateLoader resourceLoader = new StringSqlTemplateLoader(loader);
			Configuration cfg = new Configuration(ps);
			gt = new GroupTemplate(resourceLoader, cfg);
			if(product){
				loader.setProduct(product);
			}
			String charset = ps.getProperty("CHARSET");
			if (StringKit.isBlank(charset)) {
				charset = Charset.defaultCharset().name();

			}
			System.out.println("BeetlSQL 运行在 product=" + product + ",md charset=" + charset);
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
