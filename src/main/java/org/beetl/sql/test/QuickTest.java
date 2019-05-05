package org.beetl.sql.test;

import javax.sql.DataSource;

import org.beetl.sql.core.ClasspathLoader;
import org.beetl.sql.core.ConnectionSource;
import org.beetl.sql.core.ConnectionSourceHelper;
import org.beetl.sql.core.Interceptor;
import org.beetl.sql.core.SQLBatchReady;
import org.beetl.sql.core.SQLLoader;
import org.beetl.sql.core.SQLManager;
import org.beetl.sql.core.UnderlinedNameConversion;
import org.beetl.sql.core.db.MySqlStyle;
import org.beetl.sql.core.engine.PageQuery;
import org.beetl.sql.core.kit.BeanKit;
import org.beetl.sql.core.annotatoin.builder.SampleJsonAtrributeBuilder;
import org.beetl.sql.ext.DebugInterceptor;
import org.beetl.sql.ext.gen.GenConfig;
import org.beetl.sql.ext.gen.MapperCodeGen;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zaxxer.hikari.HikariDataSource;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author xiandafu
 *
 */

public class QuickTest {
	static ObjectMapper  mapper = new ObjectMapper();
	public List list = null;

    public static void main(String[] args) throws Exception {


        // DB2SqlStyle style = new DB2SqlStyle();
        // SqlServerStyle style = new SqlServerStyle();
        // SqlServer2012Style style = new SqlServer2012Style();
        // OracleStyle style = new OracleStyle();
        MySqlStyle style = new MySqlStyle();
        // PostgresStyle style = new PostgresStyle();
        ConnectionSource cs = ConnectionSourceHelper.getSingle(datasource());

        SQLLoader loader = new ClasspathLoader("/sql");
        DebugInterceptor debug = new DebugInterceptor();

        Interceptor[] inters = new Interceptor[] { debug };
        final SQLManager sql = new SQLManager(style, loader, cs, new UnderlinedNameConversion(), inters);
        //预先注册一个，否则没有办法使用@Jackson注解
        sql.getBeetl().getGroupTemplate().registerFunction("jackson", SampleJsonAtrributeBuilder.json);

        UserDao dao = sql.getMapper(UserDao.class);
////        User user = dao.selectAll1(1);
//
//        User user = dao.selectAll2(1);
//        int count = dao.getCount("lijz");

       int count =  dao.deleteUser(24);
       System.out.print(count);


//        Map paras = new HashMap();
//        paras.put("name","lijz");
//        PageQuery pageQuery = new PageQuery();
//
////        pageQuery.setParas(paras);
//        pageQuery.setPara("name","lijz");
//
//        Object obj = pageQuery.getParas();
//
//        dao.getIds3(pageQuery);
//        List<User> list = pageQuery.getList();
//        System.out.println(list.size());

      


    }
    
    static public JavaType parameterizedType(Class c,Type pt) {
    	if(pt instanceof ParameterizedType) {
    		Type[] tv = ((ParameterizedType)pt).getActualTypeArguments();
        	
        	Class[] types = new Class[tv.length];
        	for(int i=0;i<tv.length;i++) {
        		//如果还是范型
        		types[i] = ((Class)tv[i]);
        	}
        	
        	return getCollectionType(c,types);
    	}else {
    		throw new IllegalStateException(pt.toString());
    	}

    	
    	
    }
   
    

    
	

	static public JavaType getCollectionType(Class collectionClass, Class[] elementClasses) {
			
    	  return mapper.getTypeFactory().constructParametricType(collectionClass, elementClasses);
    }

    public static User unique(SQLManager sql, Object key) {
        return sql.unique(User.class, key);
    }

    public static DataSource datasource() {
        HikariDataSource ds = new HikariDataSource();
        ds.setJdbcUrl(MysqlDBConfig.url);
        ds.setUsername(MysqlDBConfig.userName);
        ds.setPassword(MysqlDBConfig.password);
        ds.setDriverClassName(MysqlDBConfig.driver);
        // ds.setAutoCommit(false);
        return ds;
    }

    public static DataSource druidSource() {
        com.alibaba.druid.pool.DruidDataSource ds = new com.alibaba.druid.pool.DruidDataSource();
        ds.setUrl(MysqlDBConfig.url);
        ds.setUsername(MysqlDBConfig.userName);
        ds.setPassword(MysqlDBConfig.password);
        ds.setDriverClassName(MysqlDBConfig.driver);
        return ds;
    }
    
   

}
