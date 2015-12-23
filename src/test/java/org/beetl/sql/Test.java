package org.beetl.sql;

import org.beetl.sql.core.ClasspathLoader;
import org.beetl.sql.core.SQLLoader;
import org.beetl.sql.core.SQLManager;
import org.beetl.sql.core.SQLScript;
import org.beetl.sql.core.db.DBStyle;
import org.beetl.sql.core.db.MySqlStyle;
import org.beetl.sql.pojo.User;

import java.util.HashMap;
import java.util.Map;

import static org.beetl.sql.core.kit.Constants.*;

/** 简单快速的功能测试类
 * @author xiandafu
 *
 */
public class Test {
	static MySqlConnectoinSource ds = new MySqlConnectoinSource();
    public static void main(String[] args) {
//    	testSimple();
//		testIf();
//    		testManager();
//    	testManagergenera();
//    	testUse();
//    	testNameConve();
//    	String a = "  \nabc  \ncc\n\n".trim();
//    	System.out.println(a);
        testGen();
	}
    public static void testGen(){
        SQLLoader loader = new ClasspathLoader("/sql/mysql");
        SQLManager manager = new SQLManager(getStyle(),loader,ds);
        System.out.println("genCondition==="+manager.getDbStyle().genCondition("user"));
        System.out.println("genColumnList==="+manager.getDbStyle().genColumnList("user"));
        System.out.println("genColAssignProperty==="+manager.getDbStyle().genColAssignProperty("user"));
        System.out.println("genColAssignPropertyAbsolute==="+manager.getDbStyle().genColAssignPropertyAbsolute("user"));
    }
    public static void testManagergenera(){
    	SQLLoader loader = new ClasspathLoader("/sql/mysql");
		SQLManager manager = new SQLManager(getStyle(),loader,ds);
		SQLScript script = manager.getScript(User.class,SELECT_BY_ID);
		System.out.println("SELECT_BY_ID==="+script.getSql());
    	script = manager.getScript(User.class,DELETE_BY_ID);
    	System.out.println("DELETE_BY_ID==="+script.getSql());
    	script = manager.getScript(User.class,SELECT_ALL);
    	System.out.println("SELECT_ALL==="+script.getSql());
    	script = manager.getScript(User.class,SELECT_BY_TEMPLATE);
    	System.out.println("SELECT_BY_TEMPLATE==="+script.getSql());
    	script = manager.getScript(User.class,UPDATE_ALL);
    	System.out.println("UPDATE_ALL==="+script.getSql());
    	script = manager.getScript(User.class,UPDATE_BY_ID);
    	System.out.println("UPDATE_BY_ID==="+script.getSql());
    	script = manager.getScript(User.class,INSERT);
    	System.out.println("INSERT==="+script.getSql());
    	script = manager.getPageSqlScript(manager.getScript(User.class,SELECT_BY_TEMPLATE).getSql());
//    	System.out.println("=====page=====\n"+script.getSql());
//    	script = manager.getCountSqlScript(manager.getScript(User.class,SELECT_BY_TEMPLATE).getSql());
    	System.out.println("=====count=====\n"+script.getSql());
    	System.out.println("=====batchUpdate=====\n"+script.getSql());
    }
    public static void testManager(){
//    	SQLLoader loader = ClasspathLoader.instance("/sql/mysql");
//		SQLManager manager = new SQLManager(getStyle(),loader,ds);
//		
//		SQLScript script = manager.getScript("user.selectUser");
//		Map<String, Object> paras = getUserParas();
//		List<User> result = script.single( paras, User.class);
//		
//		SQLScript script2 = manager.getScript(User.class,SQLManager.SELECT_BY_ID);
//		System.out.println("====sql==== \n"+script2.getSql());
//		User u = (User) script2.unique(result);//默认返回的是user.getById
//		printUser(u);
//		
//		SQLScript script3 = manager.getScript(User.class,SQLManager.UPDATE_BY_ID);//已经在30行生成了update语句
//		System.out.println("====sql==== \n"+script3.getSql());
//		result.setName("xxxx");
//		System.out.println("更新影响的行数："+script3.update(result));
		
    }
    //便于测试
	public static void printUser(User user){
		System.out.println("user:{id:"+user.getId()+",name:"+user.getName()+"}");
	}
	
	public static void testUse(){
		SQLLoader loader = new ClasspathLoader("/sql/mysql");
		SQLManager manager = new SQLManager(getStyle(),loader,ds);
		SQLScript script = manager.getScript("user.selectByExample");
//		User user = (User)script.singleSelect(new User(), User.class);
		// 
		
	}
	
	
	

	
	
	private static Map<String, Object> getUserParas(){
		User user = new User();
		user.setId(2);
		Map<String, Object> paras = new HashMap<String, Object>();
		paras.put("user", user);
		return paras;
	}
	
//	public static void testIf(){
//		String sql =" select * from user where 1=1 \n"
//				+"#if(user.name!=null){\n"
//				+"and name=${user.name}\n"
//				+"#}\n"
//				+" and id = ${user.id}";
//		User user = new User();
//		user.setId(12);
//		user.setName("xiandafu");
//		Map paras = new HashMap();
//		paras.put("user", user);
//		SQLScript script = new SQLScript("selectUserByCondition",sql,paras);
//		script.run();
//		String jdbcSQL = script.getJdbcSQL();
//		List jdbcParas = script.getJDBCParas();
//		System.out.println(jdbcSQL);
//		System.out.println(jdbcParas);
//	}
	
	
	public static DBStyle getStyle(){
		return new MySqlStyle();
	}
	
}


