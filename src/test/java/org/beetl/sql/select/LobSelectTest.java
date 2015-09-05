package org.beetl.sql.select;

import java.io.File;
import java.util.List;

import javax.imageio.stream.FileImageOutputStream;

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
public class LobSelectTest {

	private SQLLoader loader;
	private SQLManager manager;
	private String path = "/repertory/beetlsql/src/test/resources/";

	@Before public void before() {
		loader = new ClasspathLoader("/sql/mysql");
		manager = new SQLManager(new MySqlStyle(), loader, new MySqlConnectoinSource());
	}
	
	@Test public void selectLobALL(){
		List<LobBean> listLobBean = manager.all(LobBean.class);
		for(LobBean bean : listLobBean){
			System.out.println(bean.getId()+":"+bean.getArticle());
			this.byte2image(bean.getPicture(), this.path);
		}
	}
	
	@Test public void selectLobUnique(){
		LobBean bean = manager.unique(LobBean.class, 10);
		System.out.println(bean.getId()+":"+bean.getArticle());
		this.byte2image(bean.getPicture(), this.path);
	}
	
	@Test public void selectLobTemplement(){
		LobBean bean = new LobBean();
		bean.setId(10);
		List<LobBean> lobBean = manager.template(bean);
		for(LobBean b : lobBean){
			System.out.println(b.getId()+":"+b.getArticle());
			this.byte2image(b.getPicture(), this.path);
		}	
	}
	
	private void byte2image(byte[] data, String path) {
		if (data == null || data.length < 3 || path.equals("")) return;
		path = path + System.currentTimeMillis() +".png";
		try {
			FileImageOutputStream imageOutput = new FileImageOutputStream(new File(path));
			imageOutput.write(data, 0, data.length);
			imageOutput.close();
			System.out.println("图片创建成功，地址：" + path);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
}
