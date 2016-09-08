package org.beetl.sql.test;

public class MysqlDBConfig {
//	public static String driver = "com.mysql.jdbc.Driver";
//    public static String dbName = "test";
//    public static String password = "123456";
//    public static String userName = "root";
//    public static String url = "jdbc:mysql://127.0.0.1:3306/" + dbName+"?useUnicode=true&amp;characterEncoding=UTF-8";

    public static String driver = "com.microsoft.sqlserver.jdbc.SQLServerDriver";
    public static String dbName = "test";
    public static String password = "123456";
    public static String userName = "test";
    public static String url = "jdbc:sqlserver://LIJIAZHI:1833;" +  
    		   "databaseName=test;user=test;password=123456;";  ;
    
    
    
}
