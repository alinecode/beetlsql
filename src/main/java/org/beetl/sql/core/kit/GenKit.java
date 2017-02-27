package org.beetl.sql.core.kit;

import java.io.File;

public class GenKit {
	public static String getJavaSRCPath(){
		String srcPath = null;
		String userDir = System.getProperty("user.dir");
		if(userDir==null){
			throw new NullPointerException("用户目录未找到");
		}
		File src = new File(userDir,"src");
		File javaSrc = new File(src.toString(),"/main/java");
		if(javaSrc.exists()){
			srcPath = javaSrc.toString();
		}else{
			srcPath = src.toString();
		}		
		return srcPath;
	}
	
	public static String getJavaResourcePath(){
		String srcPath = null;
		String userDir = System.getProperty("user.dir");
		if(userDir==null){
			throw new NullPointerException("用户目录未找到");
		}
		File src = new File(userDir,"src");
		File resSrc = new File(src.toString(),"/main/resources");
		if(resSrc.exists()){
			srcPath = resSrc.toString();
		}else{
			srcPath = src.toString();
		}		
		return srcPath;
	}
}
