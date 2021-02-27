package org.beetl.sql.gen.simple;

import org.beetl.sql.gen.BaseProject;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

/**
 * 一个Maven工程输出,代码生成输出到工程目录下
 * @author xiandafu
 * @see org.beetl.sql.gen.SourceConfig
 * @see org.beetl.sql.gen.SourceBuilder
 * @see ConsoleOnlyProject
 */
public class SimpleMavenProject extends BaseProject {

	String basePackage = null;

	/**
	 * 指定生成java代码的包路径
	 * @param basePackage
	 */
	public SimpleMavenProject(String basePackage){
		this.basePackage =basePackage;
	}

	public SimpleMavenProject(){
		this.basePackage ="com.ibeetl.test101";
	}

	/**
	 *
	 * {@inheritDoc}
	 * @return  得到一个路径,对应maven工程下面的src
	 */
	//Override
	@Override
    public Writer getWriterByName(String sourceBuilderName, String targetName) {
		String home = this.root;
		FileWriter writer = null;
		if(sourceBuilderName.equals("md")){
			String src = this.root+ File.separator+"src/main/resources/sql";
			String output = src+File.separator+targetName;
			try {
				checkFile(output);
				writer = new FileWriter(new File(output));
			} catch (IOException e) {
				throw new IllegalArgumentException(output);
			}
		}else{

			String src = this.root+ File.separator+"src/main/java";
			String pkg = getBasePackage(sourceBuilderName);
			String subPath = pkg.replace('.',File.separatorChar);
			String output = src+File.separator+subPath+File.separator+targetName;

			try {
				checkFile(output);
				writer = new FileWriter(new File(output));
			} catch (IOException e) {
				throw new IllegalArgumentException(output,e);
			}

		}
		return writer;

	}

	@Override
	public   String getBasePackage(String sourceBuilerName){
		return basePackage+"."+sourceBuilerName;
	}
	protected void checkFile(String filePath) throws IOException {
		File file = new File(filePath);
		File fileParent = file.getParentFile();
		if (!fileParent.exists()) {
			// 创建多级目录
			fileParent.mkdirs();
		}
		if (!file.exists()) {
			//创建文件
			file.createNewFile();
		}
	}

	public void setBasePackage(String basePackage) {
		this.basePackage = basePackage;
	}
}
