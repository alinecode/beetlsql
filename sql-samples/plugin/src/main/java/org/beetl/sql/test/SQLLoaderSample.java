package org.beetl.sql.test;

import org.beetl.sql.clazz.kit.BeetlSQLException;
import org.beetl.sql.core.*;
import org.beetl.sql.core.loader.AbsolutePathLoader;
import org.beetl.sql.core.loader.MarkdownClasspathLoader;
import org.beetl.sql.core.loader.MarkdownParser;
import org.beetl.sql.core.loader.SQLFileParser;
import org.beetl.sql.ext.DebugInterceptor;
import org.beetl.sql.sample.SampleHelper;
import org.beetl.sql.sample.entity.UserEntity;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * 如何自定义一个sql加载器
 *
 *
 *
 * @author xiandafu
 */
public class SQLLoaderSample {

    public SQLLoaderSample() {

    }

    public static void main(String[] args) throws Exception {
        SQLLoaderSample loaderSample = new SQLLoaderSample();
		loaderSample.loadFromPath();
    }

    /**
     *在给执行的jdbc sql 增加一个注释
     */
    public void loadFromPath(){
        SQLManager sqlManager = SampleHelper.init();
        String root = System.getProperty("user.dir");
        String sqlPath = root+"/sql-samples/plugin/src";

		FileSQLLoader fileSQLLoader = new FileSQLLoader(sqlPath);
        sqlManager.customizedSQLLoader(fileSQLLoader);


        List<Map> all =  sqlManager.select(SqlId.of("user.all"), Map.class);
        System.out.println(all.size());

    }


	public class FileSQLLoader extends AbsolutePathLoader {
		public FileSQLLoader(String path,String charset){
			super(path,charset);

		}
		public FileSQLLoader(String path){
			super(path);
		}

		@Override
		protected URL getFile(String filePath) {
			File file = new File(filePath);
			if(!file.exists()){
				return  null;
			}
			try {
				return file.toURI().toURL();
			} catch (MalformedURLException e) {
				throw new IllegalArgumentException(filePath+" to url");
			}

		}

		@Override
		protected SQLFileParser getParser(String modelName, BufferedReader br) throws IOException {
			XMLParser parser = new XMLParser(modelName, br);
			return parser;
		}

		@Override
		protected URL getFilePath(String root, SqlId id) {
			String path = this.getPathBySqlId(id);
			String filePath0 = root + "/" + path + ".xml";
			URL is = this.getFile(filePath0);
			return is;
		}

		@Override
		public BeetlSQLException getException(SqlId sqlId){
			String path = getPathBySqlId(sqlId);


			String envInfo = path + ".xml" + " sqlLoader:" + this;
			if (existNamespace(sqlId)) {
				envInfo = envInfo + ",文件'"+sqlId.getNamespace()+".xml'找到，但没有对应的sqlId '"+sqlId.getId()+"'";
			} else {
				envInfo = envInfo + ",未找到对应的sql文件";
			}
			return new  BeetlSQLException(BeetlSQLException.CANNOT_GET_SQL, "未能找到" + sqlId + "对应的sql,搜索路径:" + envInfo);
		}

	}

	static class XMLParser implements  SQLFileParser {
		BufferedReader br;
		String namepspace;
		Iterator<Element> iterator;

		public XMLParser(String modelName, BufferedReader br){
    		this.namepspace = modelName;
    		this.br = br;
			SAXBuilder builder = new SAXBuilder();
			Document document = null;
			try {
				document = builder.build(br);
			} catch (Exception e) {
				throw new IllegalArgumentException("解析出错"+modelName);
			}
			Element root = document.getRootElement();
			iterator = root.getChildren().iterator();
		}
		@Override
		public SQLSource next() throws IOException {
			if(!iterator.hasNext()){
				return null;
			}
			Element element = iterator.next();
			String id = element.getAttributeValue("id");
			SqlId sqlId = SqlId.of(namepspace,id);
			String sqlTemplate = element.getText();
			SQLSource sqlSource = new SQLSource();
			sqlSource.setTemplate(sqlTemplate);
			sqlSource.setId(sqlId);
			//jdom解析起获取不到函数，因此beetl报错有误
			sqlSource.setLine(0);
			return sqlSource;
		}
	}

}
