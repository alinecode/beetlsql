package org.beetl.sql.gen.simple;

import org.beetl.sql.gen.BaseProject;

import java.io.OutputStreamWriter;
import java.io.Writer;

/**
 * 用于模板代码输出到控制台
 * @author xiandafu
 * @see BaseProject

 */
public class ConsoleOnlyProject extends BaseProject {
	Writer writer;
	public ConsoleOnlyProject(){
		super();
		this.writer = new OutputStreamWriter(System.out);
	}

	public ConsoleOnlyProject(Writer writer){
		this.writer = writer;
	}
	@Override
	public Writer getWriterByName(String sourceBuilderName, String targetName) {
		return writer;
	}


}
