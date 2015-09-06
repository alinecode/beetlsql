package org.beetl.sql.update;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;

import org.beetl.sql.buildsql.MySqlConnectoinSource;
import org.beetl.sql.core.ClasspathLoader;
import org.beetl.sql.core.SQLLoader;
import org.beetl.sql.core.SQLManager;
import org.beetl.sql.core.db.MySqlStyle;
import org.beetl.sql.pojo.LobBean;
import org.junit.Before;
import org.junit.Test;

/**
 * @author suxinjie
 *
 */
public class UpdateLobTest {

	private SQLLoader loader;
	private SQLManager manager;

	@Before public void before() {
		loader = new ClasspathLoader("/sql/mysql");
		manager = new SQLManager(new MySqlStyle(), loader, new MySqlConnectoinSource());
	}
	
	@SuppressWarnings({ "resource"})
	@Test public void updateLobALL(){
		LobBean lobBean = new LobBean();
		
		File file = new File("src/test/resources/blobTest.png");
		try {
			FileInputStream fis = new FileInputStream(file);
			int len = fis.available();
			byte[] buffer = new byte[len];
			fis.read(buffer, 0, len);
			
			lobBean.setPicture(buffer);
			lobBean.setArticle("小和尚不说了.太长了");
			
			int i = manager.updateAll(LobBean.class, lobBean);
			System.out.println(i);
		} catch (Exception e) {
			e.printStackTrace();
		}  
	}
	
	//======================晚上回家测试============================
	@SuppressWarnings("resource")
	@Test public void updateLob(){
		LobBean lobBean = new LobBean();
		
		File file = new File("src/test/resources/blobTest.png");
		try {
			FileInputStream fis = new FileInputStream(file);
			int len = fis.available();
			byte[] buffer = new byte[len];
			fis.read(buffer, 0, len);
			
			lobBean.setPicture(buffer);
			lobBean.setArticle("no say . too long");
			lobBean.setId(10);
			
			int i = manager.update("lobBean.updatePictureById", lobBean);
			System.out.println(i);
		} catch (Exception e) {
			e.printStackTrace();
		}  
	}
	
	@SuppressWarnings("resource")
	@Test public void updateLobById(){
		LobBean lobBean = new LobBean();
		
		File file = new File("src/test/resources/blobTest.png");
		try {
			FileInputStream fis = new FileInputStream(file);
			int len = fis.available();
			byte[] buffer = new byte[len];
			fis.read(buffer, 0, len);
			
			lobBean.setPicture(buffer);
			lobBean.setArticle("no say . too long ......");
			lobBean.setId(10);
			
			int i = manager.updateById(lobBean);
			System.out.println(i);
		} catch (Exception e) {
			e.printStackTrace();
		}  
	}
	
	@SuppressWarnings("resource")
	@Test public void updateLobBatch(){
		try {
			List<LobBean> lobBeanList = new ArrayList<LobBean>();
			for(int i=0 ;i<2 ;i++){
				LobBean lobBean = new LobBean();
				File file = new File("src/test/resources/blobTest.png");
				FileInputStream fis = new FileInputStream(file);
				int len = fis.available();
				byte[] buffer = new byte[len];
				fis.read(buffer, 0, len);
				
				lobBean.setPicture(buffer);
				lobBean.setArticle("no say . too long - "+i);
				lobBean.setId(9+i);
				
				lobBeanList.add(lobBean);
			}
			
			int[] result = manager.updateBatch("lobBean.updatePictureById", lobBeanList);
			for(int i : result){
				System.out.println(i);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}  
	}

}
//
//>>02:52:38:调用方法抛出了异常(NATIVE_CALL_EXCEPTION):isEmpty 位于8行 资源:lobbean.gen_updateAll
//java.lang.ClassCastException: [B cannot be cast to [Ljava.lang.Object;
//	at org.beetl.ext.fn.EmptyExpressionFunction.call(EmptyExpressionFunction.java:87)
//	at org.beetl.ext.fn.EmptyExpressionFunction.call(EmptyExpressionFunction.java:51)
//	at org.beetl.core.statement.FunctionExpression.evaluate(FunctionExpression.java:76)
//	at org.beetl.core.statement.NotBooleanExpression.evaluate(NotBooleanExpression.java:53)
//	at org.beetl.core.statement.IfStatement.execute(IfStatement.java:59)
//	at org.beetl.core.statement.Program.execute(Program.java:70)
//	at org.beetl.core.Template.renderTo(Template.java:137)
//	at org.beetl.core.Template.renderTo(Template.java:90)
//	at org.beetl.core.Template.render(Template.java:77)
//	at org.beetl.sql.core.SQLScript.run(SQLScript.java:64)
//	at org.beetl.sql.core.SQLScript.update(SQLScript.java:338)
//	at org.beetl.sql.core.SQLScript.update(SQLScript.java:368)
//	at org.beetl.sql.core.SQLManager.updateAll(SQLManager.java:602)
//	at org.beetl.sql.update.UpdateLobTest.updateLob(UpdateLobTest.java:44)
//	at sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method)
//	at sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:39)
//	at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:25)
//	at java.lang.reflect.Method.invoke(Method.java:597)
//	at org.junit.runners.model.FrameworkMethod$1.runReflectiveCall(FrameworkMethod.java:47)
//	at org.junit.internal.runners.model.ReflectiveCallable.run(ReflectiveCallable.java:12)
//	at org.junit.runners.model.FrameworkMethod.invokeExplosively(FrameworkMethod.java:44)
//	at org.junit.internal.runners.statements.InvokeMethod.evaluate(InvokeMethod.java:17)
//	at org.junit.internal.runners.statements.RunBefores.evaluate(RunBefores.java:26)
//	at org.junit.runners.ParentRunner.runLeaf(ParentRunner.java:271)
//	at org.junit.runners.BlockJUnit4ClassRunner.runChild(BlockJUnit4ClassRunner.java:70)
//	at org.junit.runners.BlockJUnit4ClassRunner.runChild(BlockJUnit4ClassRunner.java:50)
//	at org.junit.runners.ParentRunner$3.run(ParentRunner.java:238)
//	at org.junit.runners.ParentRunner$1.schedule(ParentRunner.java:63)
//	at org.junit.runners.ParentRunner.runChildren(ParentRunner.java:236)
//	at org.junit.runners.ParentRunner.access$000(ParentRunner.java:53)
//	at org.junit.runners.ParentRunner$2.evaluate(ParentRunner.java:229)
//	at org.junit.runners.ParentRunner.run(ParentRunner.java:309)
//调用方法出错 isEmpty
//5|if(!isEmpty(article)){
//6|	article=#article#,
//7|@}
//8|@if(!isEmpty(picture)){
//9|	picture=#picture#
//10|@}
//	at org.eclipse.jdt.internal.junit4.runner.JUnit4TestReference.run(JUnit4TestReference.java:50)
//	at org.eclipse.jdt.internal.junit.runner.TestExecution.run(TestExecution.java:38)
//	at org.eclipse.jdt.internal.junit.runner.RemoteTestRunner.runTests(RemoteTestRunner.java:459)
//	at org.eclipse.jdt.internal.junit.runner.RemoteTestRunner.runTests(RemoteTestRunner.java:675)
//	at org.eclipse.jdt.internal.junit.runner.RemoteTestRunner.run(RemoteTestRunner.java:382)
//	at org.eclipse.jdt.internal.junit.runner.RemoteTestRunner.main(RemoteTestRunner.java:192)
//org.beetl.sql.core.BeetlSQLException: SQL Script Error:>>02:52:38:调用方法抛出了异常(NATIVE_CALL_EXCEPTION):isEmpty 位于8行 资源:lobbean.gen_updateAll
//	at org.beetl.sql.core.engine.BeetlSQLTemplateExceptionHandler.processExcption(BeetlSQLTemplateExceptionHandler.java:87)
//	at org.beetl.core.Template.renderTo(Template.java:168)
//	at org.beetl.core.Template.renderTo(Template.java:90)
//	at org.beetl.core.Template.render(Template.java:77)
//	at org.beetl.sql.core.SQLScript.run(SQLScript.java:64)
//	at org.beetl.sql.core.SQLScript.update(SQLScript.java:338)
//	at org.beetl.sql.core.SQLScript.update(SQLScript.java:368)
//	at org.beetl.sql.core.SQLManager.updateAll(SQLManager.java:602)
//	at org.beetl.sql.update.UpdateLobTest.updateLob(UpdateLobTest.java:44)
//	at sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method)
//	at sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:39)
//	at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:25)
//	at java.lang.reflect.Method.invoke(Method.java:597)
//	at org.junit.runners.model.FrameworkMethod$1.runReflectiveCall(FrameworkMethod.java:47)
//	at org.junit.internal.runners.model.ReflectiveCallable.run(ReflectiveCallable.java:12)
//	at org.junit.runners.model.FrameworkMethod.invokeExplosively(FrameworkMethod.java:44)
//	at org.junit.internal.runners.statements.InvokeMethod.evaluate(InvokeMethod.java:17)
//	at org.junit.internal.runners.statements.RunBefores.evaluate(RunBefores.java:26)
//	at org.junit.runners.ParentRunner.runLeaf(ParentRunner.java:271)
//	at org.junit.runners.BlockJUnit4ClassRunner.runChild(BlockJUnit4ClassRunner.java:70)
//	at org.junit.runners.BlockJUnit4ClassRunner.runChild(BlockJUnit4ClassRunner.java:50)
//	at org.junit.runners.ParentRunner$3.run(ParentRunner.java:238)
//	at org.junit.runners.ParentRunner$1.schedule(ParentRunner.java:63)
//	at org.junit.runners.ParentRunner.runChildren(ParentRunner.java:236)
//	at org.junit.runners.ParentRunner.access$000(ParentRunner.java:53)
//	at org.junit.runners.ParentRunner$2.evaluate(ParentRunner.java:229)
//	at org.junit.runners.ParentRunner.run(ParentRunner.java:309)
//	at org.eclipse.jdt.internal.junit4.runner.JUnit4TestReference.run(JUnit4TestReference.java:50)
//	at org.eclipse.jdt.internal.junit.runner.TestExecution.run(TestExecution.java:38)
//	at org.eclipse.jdt.internal.junit.runner.RemoteTestRunner.runTests(RemoteTestRunner.java:459)
//	at org.eclipse.jdt.internal.junit.runner.RemoteTestRunner.runTests(RemoteTestRunner.java:675)
//	at org.eclipse.jdt.internal.junit.runner.RemoteTestRunner.run(RemoteTestRunner.java:382)
//	at org.eclipse.jdt.internal.junit.runner.RemoteTestRunner.main(RemoteTestRunner.java:192)

