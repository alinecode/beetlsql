package org.beetl.sql.core.engine;

/**
 * sql 参数描述，包含值，对应的名称，如
 * <pre>
 * where id=#id#
 * </pre>
 * 值是id对应的某个java对象，名字就是”id“
 * @author xiandafu
 *
 */
public class SQLParameter {
	
	public Object value;
	public String expression;
	public int type = 1;
	public static final 	int 	NAME_GENEARL = 1;
	public static final 	int 	NAME_EXPRESSION = 2;
	public static final 	int 	 NAME_UNKONW = 3;
	//默认为0，不做处理，否则，会将目标对象转成期望的方式插入到数据库，比如long转short ？？
	public int jdbcType = 0;
	
	public SQLParameter(String expression,Object value){
		this.expression = expression;
		this.value = value;
		this.type =NAME_GENEARL;
	}
	
	public SQLParameter(Object value){
		this.value = value;
		this.type = NAME_UNKONW;
		
	}
	
	public SQLParameter(String expression,Object value,int type){
		this(expression,value);
		this.type = type;
		
	}
	
	public String toString(){
		if(value!=null){
			return value.toString();
		}else{
			return "";
		}
	}

	public int getJdbcType() {
		return jdbcType;
	}

	public void setJdbcType(int jdbcType) {
		this.jdbcType = jdbcType;
	}
	
	
	
	
}
