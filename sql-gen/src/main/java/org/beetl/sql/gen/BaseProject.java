package org.beetl.sql.gen;

import lombok.Data;

import java.io.Writer;

/**
 *  Java工程的配置描述类，用于告诉{@link SourceBuilder} 如何生成代码。
 *  子类需要实现{@link #getWriterByName} 方法，返回一个适当的输出
 *  同时也可以实现{@link #getBasePackage },得到代码生成的package
 * @author xiandafu
 * @see org.beetl.sql.gen.simple.ConsoleOnlyProject
 */
@Data
public abstract  class BaseProject {
	/**
	 * 工程的根目录
	 * @see #getRoot()
	 * */
	protected  String root = null;

	public BaseProject(){

		initProjectInfo();
	}

	/**
	 *  根据名称获得一个模板的目标Writer，这个名称是{@link SourceBuilder#name}
	 * @param sourceBuilderName ，{@link SourceBuilder} 的名字，得到一个代码输出Writer，比如工程目录下，或者是控制台输出
	 * @param  targetName 目标文件名称
	 * @return
	 */
	public abstract Writer getWriterByName(String sourceBuilderName,String targetName);

	/**
	 * 返回一个{@link SourceBuilder} 对应的包名，用于java代码的包，默认返回com.test+${sourceBuilerName}
	 * 子类应该重写
	 * @param sourceBuilderName
	 * @return
	 */
	public   String getBasePackage(String sourceBuilderName){
		return "com.test."+sourceBuilderName;
	}

	/**
	 * 初始化工程信息
	 */
	protected  void initProjectInfo(){
		root = System.getProperty("user.dir");
	}


}
