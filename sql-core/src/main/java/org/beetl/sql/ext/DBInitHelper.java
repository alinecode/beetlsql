package org.beetl.sql.ext;

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
			InputStream ins = sqlManager.getClassLoaderKit().loadResource(sqlFile);
			if(ins==null){
				throw new IllegalArgumentException("sql script 不存在 "+sqlFile);
			}
			int len = ins.available();
			byte[] bs = new byte[len];
			ins.read(bs);
			String str = new String(bs,"UTF-8");
			String[] sqls = str.split(";");
			defaultConnectionSource = (DefaultConnectionSource)sqlManager.getDs();
			conn = defaultConnectionSource.getMasterConn();
			executeSql(conn,sqls);
			if(defaultConnectionSource.getSlaves()!=null){
				for(DataSource salve:defaultConnectionSource.getSlaves()){
					executeSql(salve.getConnection(),sqls);
				}
			}

		}catch(Exception ex){
			throw new RuntimeException(ex);
		}finally {
			defaultConnectionSource.closeConnection(conn,null,true);
		}
	}

	private static void executeSql(Connection conn,String[] sqls) throws SQLException {
		for(String sql:sqls){
			PreparedStatement ps = conn.prepareStatement(sql);
			ps.executeUpdate();
			ps.close();
		}
	}

}
