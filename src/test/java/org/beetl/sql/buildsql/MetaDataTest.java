/**  
 * 文件名:    ManagerGeneraTest.java  
 * 描述:      
 * 作者:      suxj
 * 版本:      1.0  
 * 创建时间:  2015年8月14日 下午1:52:18  
 *  
 * 修改历史:  
 * 日期                          作者           版本         描述  
 * ------------------------------------------------------------------  
 * 2015年8月14日        suxj     1.0     1.0 Version  
 */ 
package org.beetl.sql.buildsql;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.beetl.sql.MySqlConnectoinSource;
import org.junit.Before;
import org.junit.Test;

/**  
 * @ClassName: ManagerGeneraTest   
 * @Description: 生成sql模板测试  
 * @author: suxj  
 * @date:2015年8月14日 下午1:52:18     
 */
public class MetaDataTest {
	MySqlConnectoinSource source = new MySqlConnectoinSource();

	@Before
	public void before(){
		}

	@Test
	public void test() throws SQLException {
		Connection conn = source.getMaster();
		DatabaseMetaData dbmd = conn.getMetaData();
		ResultSet rs = dbmd.getTables(null, "%",  "%",
				new String[] { "TABLE" });
		while(rs.next()){
			System.out.println(rs.getObject(3));
		}
	}
	
}
