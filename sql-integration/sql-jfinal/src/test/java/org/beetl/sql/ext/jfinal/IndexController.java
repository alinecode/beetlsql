package org.beetl.sql.ext.jfinal;

import com.jfinal.aop.Before;
import com.jfinal.core.Controller;
import org.beetl.sql.core.SQLManager;

public class IndexController extends Controller {
	@Before(Trans.class)
	public void index() {

		SQLManager sqlManager = JFinalBeetlSql.dao();
		JFinalUser user = sqlManager.unique(JFinalUser.class,1);
		renderText("hello,go "+user.getName());
	}
}
