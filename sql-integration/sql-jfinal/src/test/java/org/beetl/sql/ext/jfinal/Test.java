package org.beetl.sql.ext.jfinal;

import com.jfinal.config.*;
import com.jfinal.kit.PropKit;
import com.jfinal.server.undertow.UndertowServer;
import com.jfinal.template.Engine;
import org.beetl.sql.core.SQLManager;
import org.beetl.sql.ext.DBInitHelper;

import java.util.Properties;

public class Test extends JFinalConfig {

	/**
	 * 配置常量
	 */
	public void configConstant(Constants me) {
		me.setDevMode(true);
	}

	/**
	 * 配置路由
	 */
	public void configRoute(Routes me) {
		me.add("/", IndexController.class, "/index");	// 第三个参数为该Controller的视图存放路径
	}

	@Override
	public void configEngine(Engine engine) {

	}

	/**
	 * 配置插件
	 */
	public void configPlugin(Plugins me) {
		Properties ps = PropKit.use("config.properties").getProperties();
		JFinalBeetlSql.init(ps);
		//非必要代码，初始化一个H2数据库做测试
		SQLManager sqlManager = JFinalBeetlSql.dao();
		DBInitHelper.executeSqlScript(sqlManager,"db/schema.sql");


	}

	/**
	 * 配置全局拦截器
	 */
	public void configInterceptor(Interceptors me) {
		me.addGlobalActionInterceptor(new Trans());
	}

	/**
	 * 配置处理器
	 */
	public void configHandler(Handlers me) {

	}

	public static void main(String[] args) {
		UndertowServer.start(Test.class, 80, true);

	}
}
