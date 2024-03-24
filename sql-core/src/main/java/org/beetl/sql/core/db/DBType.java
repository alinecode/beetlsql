package org.beetl.sql.core.db;

public class DBType {
	/*支持的id类型*/
	public static final int ID_UNKNOWN = -1;
    public static final int ID_ASSIGN = 1;
    public static final int ID_AUTO = 2;
    public static final int ID_SEQ = 3;

    /*目前支持的数据库*/
    public static final int DB_MYSQL = 1;
    public static final int DB_ORACLE = 2;
    public static final int DB_POSTGRES = 3;
    public static final int DB_SQLSERVER = 4;
    public static final int DB_SQLLITE = 5;
    public static final int DB_DB2 = 6;
    public static final int DB_H2 = 7;
    //国产数据库,真的越来越多
    public static final int DB_DAMENG = 8;
    public static final int DB_SHENGTONG = 9;
    public static final int DB_HUAWEI_OPENGAUSS = 10;
    public static final int DB_KINGBASE = 11;
    public static final int DB_TAOS = 12;
	public static final int DB_ALIYUN_POLARDB = 13;
	public static final int DB_GREATSQL = 14;
	public static final int DB_GBASE8S = 15;
    public static final int DB_DERBY = 20;
    /**
     * 成熟开源MPP数据库
     */
    public static final int DB_GREENPLUM = 21;

	public static final int DB_OCEANBASE= 22;

    /*NOSQL 数据库，从100开始*/
    public static final int DB_CASSANDRA = 101;
    public static final int DB_CLICKHOUSE = 102;
    public static final int DB_DRILL = 103;
    public static final int DB_ES = 104;
    public static final int PRESTO_ES = 105;
    public static final int DB_HBASE = 106;
    public static final int DB_HIVE = 107;
    public static final int DB_DRUID = 108;
	public static final int DB_IOTDB = 109;
	public static final int DB_DORIS = 110;

	public static final int DB_TRINO= 111;

	public static final int DB_OPENPLANT = 112;

	public static final int DB_YMARTIX = 113;

    /*内存数据 从200开始*/
    public static final int DB_IGNITE = 201;
    public static final int DB_COUCHBASE = 202;
}
