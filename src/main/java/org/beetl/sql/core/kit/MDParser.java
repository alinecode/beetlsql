package org.beetl.sql.core.kit;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import org.beetl.sql.core.SQLSource;
/**
 * 解析md文档，文档格式参考beetlsql文档
 * @author xiandafu
 *
 */
public class MDParser {
	BufferedReader br;
	String modelName ;
	int linNumber ;
	String lastLine;
	String lastlastLine;
	int status = 0;
	private static int END = 1;
	protected static String lineSeparator = System.getProperty("line.separator", "\n");
	public MDParser(String modelName,BufferedReader br) throws IOException{
		this.modelName =  modelName;
		this.br = br;
		skipHeader();
	}
	
	
	public void skipHeader() throws IOException{
		while(true){
			String line = nextLine();
			if(status==END){
				return ;
			}
			if(line.startsWith("===")){
				return ;
			}
			
		}
	}
	
	public SQLSource next() throws IOException{
		String sqlId = readSqlId();
		if(status==END){
			return null;
		}
		//去掉可能的尾部空格
		sqlId = sqlId.trim();
		skipComment();
		if(status==END){
			return null;
		}
		int sqlLine = this.linNumber;
		String sql = readSql();
		
		SQLSource source = new SQLSource(modelName + sqlId,sql);
		source.setLine(sqlLine);
		return source;
	}
	
	
	public void skipComment() throws IOException{
		boolean findComment = false ;
		while(true){
			String line = nextLine();
			if(status==END){
				return ;
			}
			line = line.trim();
			if(!findComment&&line.length()==0){
				continue ;
			}
			if(line.startsWith("*")){
				//注释符号
				findComment = true;
				continue;
			}else {
				String s = line.trim();
				if(s.length()==0){
					continue;
				}
				else if(s.startsWith("```")||s.startsWith("~~~")){
					//忽略以code block开头的符号
					continue;
				}else{
					//注释结束
					return ;
				}
				
			}
		}
	}
	
	public String readSql() throws IOException{
		List<String> list = new LinkedList<String>();
		list.add(lastLine);
		while(true){
			String line = nextLine();
			
			if(status==END){
				return getBuildSql(list);
			}
			
			if(line.startsWith("===")){
				//删除下一个sqlId表示
				list.remove(list.size()-1);
				return getBuildSql(list);
			}
			list.add(line);
			
		}
	}
	private String getBuildSql(List<String> list){
		StringBuilder sb = new StringBuilder();
		for(String str:list){
			String s = str.trim();
			if(s.startsWith("```")||s.startsWith("~~~")){
				//忽略以code block开头的符号
				continue;
			}
			sb.append(str).append(lineSeparator);
		}
		return sb.toString();
	}
	
	public String readSqlId(){
		return lastlastLine;
	}
	
	public String nextLine() throws IOException {
		String line = br.readLine();
		linNumber++;
		if(line==null){
			status = END;
			
		}
		//保存最后读的俩行
		lastlastLine = lastLine;
		lastLine = line;
		return line;
	}
	
	
	
	
	
	
}
