package org.beetl.sql.core.loader;

import org.beetl.sql.clazz.SQLType;
import org.beetl.sql.clazz.kit.ClassLoaderKit;
import org.beetl.sql.clazz.kit.Plugin;
import org.beetl.sql.core.SQLSource;
import org.beetl.sql.core.SqlId;

import java.io.*;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 从classpath系统加载sql模板，id应该格式是"xx.yyy",xx代表了文件名，yyy代表了sql标识 sql 模板格式如下：
 *
 * <pre>
 * selectUser
 * ===
 *
 * select * from user
 * </pre>
 * <p>
 * 可以重载此类，实现{@code #getParser}  用于解析其他格式文件，比如xml格式的sql文件
 *
 * @author Administrator
 */
@Plugin
public class MarkdownClasspathLoader extends PathLoader {

	protected ClassLoaderKit classLoaderKit;

	public ClassLoaderKit getClassLoaderKit() {
		return classLoaderKit;
	}

	public void setClassLoaderKit(ClassLoaderKit classLoaderKit) {
		this.classLoaderKit = classLoaderKit;
	}


    public MarkdownClasspathLoader(String root, String charset) {
       super(root,charset);

    }

    public MarkdownClasspathLoader(String root) {
       super(root);

    }

    public MarkdownClasspathLoader() {
        this("sql");
    }



    @Override
    protected URL getFile(String filePath) {
        return classLoaderKit.loadResourceAsURL(filePath);

    }
    @Override
	protected SQLFileParser getParser(String modelName, BufferedReader br) throws IOException {
		MarkdownParser parser = new MarkdownParser(modelName, br);
		return parser;
	}


}

