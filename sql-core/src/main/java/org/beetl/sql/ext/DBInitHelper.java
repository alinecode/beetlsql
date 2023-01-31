package org.beetl.sql.ext;

import org.beetl.ext.fn.StringUtil;
import org.beetl.sql.clazz.kit.StringKit;
import org.beetl.sql.core.DefaultConnectionSource;
import org.beetl.sql.core.SQLManager;

import javax.sql.DataSource;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * 初始化sqlManager库.用来测试用的
 * @author xiandafu
 */
public class DBInitHelper {

	public static void executeSqlScript(SQLManager sqlManager,String  sqlFile){
		Connection conn = null;
		DefaultConnectionSource defaultConnectionSource = null;
		try{
			defaultConnectionSource = (DefaultConnectionSource)sqlManager.getDs();
			InputStream ins = sqlManager.getClassLoaderKit().loadResource(sqlFile);
			if(ins==null){
				throw new IllegalArgumentException("sql script 不存在 "+sqlFile);
			}
			int len = ins.available();
			byte[] bs = new byte[len];
			ins.read(bs);
			String str = new String(bs,"UTF-8");
			String[] sqls = str.split(";");
			conn = defaultConnectionSource.getMasterConn();
			executeSql(conn,sqls);
			if(defaultConnectionSource.getSlaves()!=null){
				for(DataSource salve:defaultConnectionSource.getSlaves()){
					executeSql(salve.getConnection(),sqls);
				}
			}
			sqlManager.getMetaDataManager().refresh();

		}catch(Exception ex){
			throw new RuntimeException(ex);
		}finally {
			defaultConnectionSource.closeConnection(conn,null,true);
		}
	}

	private static void executeSql(Connection conn,String[] sqls) throws SQLException {
		for(String sql:sqls){
			if(StringKit.isBlank(sql)){
				continue;
			}
			PreparedStatement ps = conn.prepareStatement(sql);
			ps.executeUpdate();
			ps.close();
		}
	}

}
