package org.beetl.sql.core;

import org.apache.commons.io.FileUtils;
import org.beetl.sql.BaseTest;
import org.beetl.sql.core.db.H2Style;
import org.beetl.sql.core.engine.NoneBlockStringReader;
import org.beetl.sql.core.engine.SqlTemplateResource;
import org.beetl.sql.core.engine.StringSqlTemplateLoader;
import org.beetl.sql.core.loader.SQLLoader;
import org.beetl.sql.core.nosql.DruidStyle;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.io.Reader;

public class SqlLoaderTest extends BaseTest {

    @BeforeClass
    public static void init() {
        initTable(testSqlFile);
    }



    @Test
    public void loadSqlFromMd() {
        SqlId id =SqlId.of("user","queryById");
        SQLLoader loader = sqlManager.getSqlLoader();
        loader.setDbStyle(new DruidStyle());
        SQLSource sqlSource = loader.querySQL(id);
        Assert.assertNotNull(sqlSource);
		//切换数据库
        sqlManager.setDbStyle(new DruidStyle());
		SqlId id2 =SqlId.of("user","queryById");
		SQLSource sqlSource2 = loader.querySQL(id2);
		Assert.assertNotNull(sqlSource2);
		Assert.assertTrue(sqlSource2.getTemplate().contains("/* from druid */"));

        SqlId id3 =SqlId.of("user","druidxxxx");
        SQLSource sqlSource3 = loader.querySQL(id3);
        Assert.assertNotNull(sqlSource3);
        Assert.assertTrue(sqlSource3.getTemplate().contains("/* from druid */"));






    }

	@Test
	public void beetlResourceCheck() throws IOException{
		H2Style h2Style = new H2Style();
		DruidStyle druidStyle =new DruidStyle();
		sqlManager.setDbStyle(h2Style);

		SQLLoader loader = sqlManager.getSqlLoader();
		loader.setDbStyle(h2Style);

		StringSqlTemplateLoader beetlLoader = new StringSqlTemplateLoader(loader);
		SqlId id =SqlId.of("user","queryById");
		SqlTemplateResource sqlTemplateResource = (SqlTemplateResource)beetlLoader.getResource(id);
		NoneBlockStringReader reader = (NoneBlockStringReader)sqlTemplateResource.openReader();
		String content = reader.getContent();
		Assert.assertFalse(content.contains("/* from druid */"));

		sqlManager.setDbStyle(druidStyle);
		loader.setDbStyle(druidStyle);

		sqlTemplateResource = (SqlTemplateResource)beetlLoader.getResource(id);
		reader = (NoneBlockStringReader)sqlTemplateResource.openReader();
		content = reader.getContent();
		Assert.assertTrue(content.contains("/* from druid */"));

		Assert.assertFalse(loader.isModified(id));


	}


	@Test
	public void beetlVersionCheck() throws IOException{


		DruidStyle druidStyle =new DruidStyle();
		SQLLoader loader = sqlManager.getSqlLoader();
		sqlManager.setDbStyle(druidStyle);
		loader.setDbStyle(druidStyle);

		StringSqlTemplateLoader beetlLoader = new StringSqlTemplateLoader(loader);

		SqlId id =SqlId.of("user","queryById");

		SqlTemplateResource sqlTemplateResource = (SqlTemplateResource)beetlLoader.getResource(id);
		NoneBlockStringReader reader = (NoneBlockStringReader)sqlTemplateResource.openReader();
		modify();
		boolean isModified = sqlTemplateResource.isModified();
		Assert.assertTrue(isModified);

		reader = (NoneBlockStringReader)sqlTemplateResource.openReader();
		reset();
		String content = reader.getContent();
		Assert.assertTrue(content.contains("versionUpdate"));



	}

	private void modify() throws IOException {
		String rootPath = System.getProperty("user.dir")+File.separator+"target"+File.separator+"test-classes/sql/druid";
		File old = new File(rootPath,"user.md");
		File backup = new File(rootPath,"user.md.backup");
		FileUtils.copyFile(old,backup);
		File newFile = new File(rootPath,"user-newversion.txt");
		FileUtils.copyFile(newFile,old);


	}

	private void reset() throws IOException {
		String rootPath = System.getProperty("user.dir")+File.separator+"target"+File.separator+"test-classes/sql/druid";
		File old = new File(rootPath,"user.md");
		File backup = new File(rootPath,"user.md.backup");
		FileUtils.copyFile(backup,old);

	}







}
