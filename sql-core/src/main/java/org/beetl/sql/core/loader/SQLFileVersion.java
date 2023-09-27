package org.beetl.sql.core.loader;

import java.net.URL;
/**
 * 记录sql的版本号，如果为0，表示无版本，比如md文件在jar里
 */

public  class SQLFileVersion {
	/**
	 * sql资源URL表示
	 */
	public URL url;
	/** 根目录下sql文件版本 */
	public long root = 0L;
	/** 具体db下的 */
	public long db = 0L;

	public boolean isModified(SQLFileVersion newVersion) {
		return newVersion.root != root || newVersion.db != db;
	}

	public SQLFileVersion newVersion(){
		SQLFileVersion sqlFileVersion = new SQLFileVersion();
		sqlFileVersion.url = url;
		if(root!=0){
			sqlFileVersion.root = 1;
		}

		if(db!=0){
			sqlFileVersion.db = 1;
		}

		return sqlFileVersion;
	}
}
