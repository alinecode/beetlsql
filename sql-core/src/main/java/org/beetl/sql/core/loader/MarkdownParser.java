package org.beetl.sql.core.loader;

import org.beetl.sql.core.SqlId;
import org.beetl.sql.core.SQLSource;
import org.beetl.sql.clazz.kit.Plugin;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

/**
 * 解析md文档，文档格式参考beetlsql文档
 * @author xiandafu
 *
 */
@Plugin
public class MarkdownParser implements SQLFileParser {
	BufferedReader br;
	String namepspace;
	int linNumber ;
	//最后一行内容
	String lastLine;
	//倒数第二行内容
	String penultimateLine;
	int status = 0;
	private static int END = 1;
	protected static String lineSeparator = System.getProperty("line.separator", "\n");

	protected boolean  inBody = false;

	public MarkdownParser(String namepspace, BufferedReader br) throws IOException{
		this.namepspace =  namepspace;
		this.br = br;
		skipHeader();
	}
	
	
	protected void skipHeader() throws IOException{
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

	@Override
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
		SqlId newId = SqlId.of(namepspace,sqlId);
		
		SQLSource source = new SQLSource(newId,sql);
		source.setLine(sqlLine);

		inBody = false;
		return source;
	}


	protected void skipComment() throws IOException{
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
			if(!inBody&&line.startsWith("*")){
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
					inBody = true;
					continue;
				}else{
					//注释结束
					return ;
				}
				
			}
		}
	}

	protected String readSql() throws IOException{
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
	protected String getBuildSql(List<String> list){
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

	protected String readSqlId(){
		return penultimateLine;
	}

	protected String nextLine() throws IOException {
		String line = br.readLine();
		linNumber++;
		if(line==null){
			status = END;
			
		}
		//保存最后读的俩行
		penultimateLine = lastLine;
		lastLine = line;
		return line;
	}
	
	
	
	
	
	
}
