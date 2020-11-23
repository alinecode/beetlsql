package org.beetl.sql.gen.simple;

import org.beetl.sql.gen.BaseProject;

import java.io.StringWriter;
import java.io.Writer;

/**
 * 把代码生成的输出保存到字符串里，不像ConsoleOnlyProject那样输出到控制台，
 * 或者SimpleMavenProject那样写到工程文件里。
 * @author xiandafu
 * @see ConsoleOnlyProject

 */
public class StringOnlyProject extends BaseProject {
	Writer writer;
	public StringOnlyProject(){
		super();
		this.writer = new StringWriter();
	}

	public StringOnlyProject(Writer writer){
		this.writer = writer;
	}
	@Override
	public Writer getWriterByName(String sourceBuilderName, String targetName) {
		return writer;
	}

	/**
	 * 返回内容
	 * @return
	 */
	public String getContent(){
		return writer.toString();
	}


}
