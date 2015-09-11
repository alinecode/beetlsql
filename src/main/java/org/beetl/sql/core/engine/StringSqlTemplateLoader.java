package org.beetl.sql.core.engine;

import org.beetl.core.GroupTemplate;
import org.beetl.core.Resource;
import org.beetl.core.ResourceLoader;
import org.beetl.sql.core.SQLLoader;
import org.beetl.sql.core.SQLSource;

public class StringSqlTemplateLoader implements ResourceLoader {
	SQLLoader sqlLoader;
	boolean autoCheck = true ;
	public StringSqlTemplateLoader (SQLLoader sqlLoader,boolean autoCheck){
		this.sqlLoader = sqlLoader;
	}
	@Override
	public Resource getResource(String key) {
		SQLSource source = sqlLoader.getSQL(key);
		return new SqlTemplateResource(key,source,this);
	}

	@Override
	public boolean isModified(Resource key) {
		if( autoCheck) return  key.isModified() ;
		else return false ;

	}

	@Override
	public boolean exist(String key) {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public void close() {
		// TODO Auto-generated method stub

	}

	@Override
	public void init(GroupTemplate gt) {
		//never use
	}

	@Override
	public String getResourceId(Resource resource, String key) {
		//never use
		return null;
	}
	
	
	protected SQLLoader getSqlLLoader() {
		return sqlLoader;
	}


}
