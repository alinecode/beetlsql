package org.beetl.sql.springboot.dynamicds;

import org.beetl.sql.clazz.kit.StringKit;

import javax.sql.DataSource;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * 初始化数据库
 */
public class DBInitTool {
	public  void executeSqlScript(DataSource ds, InputStream sqlFile){
		Connection conn = null;
		try{

			int len = sqlFile.available();
			byte[] bs = new byte[len];
			sqlFile.read(bs);
			String str = new String(bs,"UTF-8");
			String[] sqls = str.split(";");
			conn = ds.getConnection();
			executeSql(conn,sqls);
			conn.commit();

		}catch(Exception ex){

			throw new RuntimeException(ex);
		}finally {
			if(conn!=null){
				try {
					conn.close();
				} catch (SQLException e) {
					throw new RuntimeException(e);
				}
			}
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
