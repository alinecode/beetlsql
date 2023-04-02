package org.beetl.sql.xml;

import org.beetl.sql.clazz.kit.BeetlSQLException;
import org.beetl.sql.core.SqlId;
import org.beetl.sql.core.loader.MarkdownClasspathLoader;
import org.beetl.sql.core.loader.MarkdownParser;
import org.beetl.sql.core.loader.PathLoader;
import org.beetl.sql.core.loader.SQLFileParser;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.URL;

public class XMLClasspathLoader extends MarkdownClasspathLoader {

	public XMLClasspathLoader(String root, String charset) {
		super(root,charset);

	}

	public XMLClasspathLoader(String root) {
		super(root);

	}


	public XMLClasspathLoader() {
		this("sql");
	}

	@Override
	protected URL getFile(String filePath) {
		return classLoaderKit.loadResourceAsURL(filePath);

	}
	@Override
	protected SQLFileParser getParser(String modelName, BufferedReader br) throws IOException {
		XMLFileParser parser = new XMLFileParser(modelName, br);
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
			envInfo = envInfo + ",文件找到，但没有对应的sqlId";
		} else {
			envInfo = envInfo + ",未找到对应的sql文件";
		}
		return new  BeetlSQLException(BeetlSQLException.CANNOT_GET_SQL, "未能找到" + sqlId + "对应的sql,搜索路径:" + envInfo);
	}
}
