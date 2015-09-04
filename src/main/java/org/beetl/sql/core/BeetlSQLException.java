package org.beetl.sql.core;

public class BeetlSQLException extends RuntimeException {
	
	private static final long serialVersionUID = -6315329503841905147L;
	
	public static final int 	CANNOT_GET_CONNECTION  = 0;
	public static final int 	SQL_EXCEPTION  = 1;
	public static final int 	CANNOT_GET_SQL  = 2;
	public static final int 	MAPPING_ERROR  = 3;
	//UNQUE 方法需要传入主键的个数与数据库期望的主键个数不一致
	public static final int 	ID_EXPECTED_ONE_ERROR  = 4;
	
	//UNQUE 方法需要传入主键的个数与数据库期望的主键个数不一致
	public static final int 	NOT_UNIQUE_ERROR  = 5;
	
	//SQL 脚本运行出错
	public static final int 	SQL_SCRIPT_ERROR  = 6;
	
	
	//根据指定类创建实例出错
	public static final int 	OBJECT_INSTANCE_ERROR  = 7;
		
		
	int code ;
	
	public BeetlSQLException(int code){
		this.code = code;
	}
	
	public BeetlSQLException(int code,Exception e){
		super(e);
		this.code = code;
	}
	
	public BeetlSQLException(int code,String msg,Exception e){
		super(msg,e);
		this.code = code;
	}
	
	public BeetlSQLException(int code,String msg){
		super(msg);
		this.code = code;
	}
}
