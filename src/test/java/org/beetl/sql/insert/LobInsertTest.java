package org.beetl.sql.insert;

import java.io.File;
import java.io.FileInputStream;

import org.beetl.sql.buildsql.MySqlConnectoinSource;
import org.beetl.sql.core.ClasspathLoader;
import org.beetl.sql.core.SQLLoader;
import org.beetl.sql.core.SQLManager;
import org.beetl.sql.core.db.KeyHolder;
import org.beetl.sql.core.db.MySqlStyle;
import org.beetl.sql.pojo.LobBean;
import org.junit.Before;
import org.junit.Test;

/**
 * @author suxinjie
 *
 */
public class LobInsertTest {

	private SQLLoader loader;
	private SQLManager manager;

	@Before
	public void before() {
		loader = new ClasspathLoader("/sql/mysql");
		manager = new SQLManager(new MySqlStyle(), loader, new MySqlConnectoinSource());
	}
	
	@Test
	public void insertText(){
		String text = "从前有座山，山里 有座庙，庙里有个小和尚，小和尚对老和尚说：	从前有座山，山里 有座庙，庙里有个小和尚，小和尚对老和尚说：	从前有座山，山里 有座庙，庙里有个小和尚，小和尚对老和尚说："
				+ "	从前有座山，山里 有座庙，庙里有个小和尚，小和尚对老和尚说：	从前有座山，山里 有座庙，庙里有个小和尚，小和尚对老和尚说：	从前有座山，山里 有座庙，庙里有个小和尚，小和尚对老和尚说："
				+ "	从前有座山，山里 有座庙，庙里有个小和尚，小和尚对老和尚说：	从前有座山，山里 有座庙，庙里有个小和尚，小和尚对老和尚说：	从前有座山，山里 有座庙，庙里有个小和尚，小和尚对老和尚说：	从前有座山，山里 有座庙，庙里有个小和尚，小和尚对老和尚说："
				+ "	从前有座山，山里 有座庙，庙里有个小和尚，小和尚对老和尚说：	从前有座山，山里 有座庙，庙里有个小和尚，小和尚对老和尚说：	从前有座山，山里 有座庙，庙里有个小和尚，小和尚对老和尚说：	从前有座山，山里 有座庙，庙里有个小和尚，小和尚对老和尚说："
				+ "	从前有座山，山里 有座庙，庙里有个小和尚，小和尚对老和尚说：	从前有座山，山里 有座庙，庙里有个小和尚，小和尚对老和尚说：	从前有座山，山里 有座庙，庙里有个小和尚，小和尚对老和尚说：	从前有座山，山里 有座庙，庙里有个小和尚，小和尚对老和尚说："
				+ "	从前有座山，山里 有座庙，庙里有个小和尚，小和尚对老和尚说：	从前有座山，山里 有座庙，庙里有个小和尚，小和尚对老和尚说：	从前有座山，山里 有座庙，庙里有个小和尚，小和尚对老和尚说：	从前有座山，山里 有座庙，庙里有个小和尚，小和尚对老和尚说："
				+ "	从前有座山，山里 有座庙，庙里有个小和尚，小和尚对老和尚说：	从前有座山，山里 有座庙，庙里有个小和尚，小和尚对老和尚说：	从前有座山，山里 有座庙，庙里有个小和尚，小和尚对老和尚说：	从前有座山，山里 有座庙，庙里有个小和尚，小和尚对老和尚说："
				+ "	从前有座山，山里 有座庙，庙里有个小和尚，小和尚对老和尚说：	从前有座山，山里 有座庙，庙里有个小和尚，小和尚对老和尚说：	从前有座山，山里 有座庙，庙里有个小和尚，小和尚对老和尚说：	从前有座山，山里 有座庙，庙里有个小和尚，小和尚对老和尚说："
				+ "	从前有座山，山里 有座庙，庙里有个小和尚，小和尚对老和尚说：	从前有座山，山里 有座庙，庙里有个小和尚，小和尚对老和尚说：	从前有座山，山里 有座庙，庙里有个小和尚，小和尚对老和尚说：	从前有座山，山里 有座庙，庙里有个小和尚，小和尚对老和尚说：";
		
		LobBean lobBean = new LobBean();
		lobBean.setArticle(text);
		KeyHolder holder = new KeyHolder();
		manager.insert(LobBean.class, lobBean, holder);
		System.out.println(holder.getKey());
	}
	
	@SuppressWarnings({ "resource", "unused" })
	@Test
	public void insertBlob(){
		LobBean lobBean = new LobBean();
		
		File file = new File("/repertory/beetlsql/src/test/resources/blobTest.png");
		try {
			FileInputStream fis = new FileInputStream(file);
			byte[] buffer = new byte[1024 * 1024];
			int len = fis.read(buffer, 0, buffer.length);
			lobBean.setPicture(buffer);
			
			KeyHolder holder = new KeyHolder();
			manager.insert(LobBean.class, lobBean, holder);
			System.out.println(holder.getKey());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}  
	}
	
	@SuppressWarnings({ "resource", "unused" })
	@Test
	public void insertLob(){
		LobBean lobBean = new LobBean();
		
		String text = "从前有座山，山里 有座庙，庙里有个小和尚，小和尚对老和尚说：	从前有座山，山里 有座庙，庙里有个小和尚，小和尚对老和尚说：	从前有座山，山里 有座庙，庙里有个小和尚，小和尚对老和尚说："
				+ "	从前有座山，山里 有座庙，庙里有个小和尚，小和尚对老和尚说：	从前有座山，山里 有座庙，庙里有个小和尚，小和尚对老和尚说：	从前有座山，山里 有座庙，庙里有个小和尚，小和尚对老和尚说："
				+ "	从前有座山，山里 有座庙，庙里有个小和尚，小和尚对老和尚说：	从前有座山，山里 有座庙，庙里有个小和尚，小和尚对老和尚说：	从前有座山，山里 有座庙，庙里有个小和尚，小和尚对老和尚说：	从前有座山，山里 有座庙，庙里有个小和尚，小和尚对老和尚说："
				+ "	从前有座山，山里 有座庙，庙里有个小和尚，小和尚对老和尚说：	从前有座山，山里 有座庙，庙里有个小和尚，小和尚对老和尚说：	从前有座山，山里 有座庙，庙里有个小和尚，小和尚对老和尚说：	从前有座山，山里 有座庙，庙里有个小和尚，小和尚对老和尚说："
				+ "	从前有座山，山里 有座庙，庙里有个小和尚，小和尚对老和尚说：	从前有座山，山里 有座庙，庙里有个小和尚，小和尚对老和尚说：	从前有座山，山里 有座庙，庙里有个小和尚，小和尚对老和尚说：	从前有座山，山里 有座庙，庙里有个小和尚，小和尚对老和尚说："
				+ "	从前有座山，山里 有座庙，庙里有个小和尚，小和尚对老和尚说：	从前有座山，山里 有座庙，庙里有个小和尚，小和尚对老和尚说：	从前有座山，山里 有座庙，庙里有个小和尚，小和尚对老和尚说：	从前有座山，山里 有座庙，庙里有个小和尚，小和尚对老和尚说："
				+ "	从前有座山，山里 有座庙，庙里有个小和尚，小和尚对老和尚说：	从前有座山，山里 有座庙，庙里有个小和尚，小和尚对老和尚说：	从前有座山，山里 有座庙，庙里有个小和尚，小和尚对老和尚说：	从前有座山，山里 有座庙，庙里有个小和尚，小和尚对老和尚说："
				+ "	从前有座山，山里 有座庙，庙里有个小和尚，小和尚对老和尚说：	从前有座山，山里 有座庙，庙里有个小和尚，小和尚对老和尚说：	从前有座山，山里 有座庙，庙里有个小和尚，小和尚对老和尚说：	从前有座山，山里 有座庙，庙里有个小和尚，小和尚对老和尚说："
				+ "	从前有座山，山里 有座庙，庙里有个小和尚，小和尚对老和尚说：	从前有座山，山里 有座庙，庙里有个小和尚，小和尚对老和尚说：	从前有座山，山里 有座庙，庙里有个小和尚，小和尚对老和尚说：	从前有座山，山里 有座庙，庙里有个小和尚，小和尚对老和尚说：";

		File file = new File("/repertory/beetlsql/src/test/resources/blobTest.png");
		try {
			FileInputStream fis = new FileInputStream(file);
			byte[] buffer = new byte[1024 * 1024];
			int len = fis.read(buffer, 0, buffer.length);
			
			lobBean.setPicture(buffer);
			lobBean.setArticle(text);
			
			KeyHolder holder = new KeyHolder();
			manager.insert(LobBean.class, lobBean, holder);
			System.out.println(holder.getKey());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}  
	}
	
}
