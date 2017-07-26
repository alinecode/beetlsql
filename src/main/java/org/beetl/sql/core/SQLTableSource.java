package org.beetl.sql.core;

import java.util.Map;

import org.beetl.sql.core.annotatoin.AssignID;
import org.beetl.sql.core.db.TableDesc;

/**
 * 内置的SQLSource，如CRUD
 * @author xiandafu
 *
 */
public class SQLTableSource extends SQLSource {
	
	
	protected TableDesc tableDesc;
	
	protected Map<String,AssignID> assignIds;
	protected int idType;
	
	public SQLTableSource(String template) {

		this.template = template;
	}
	
	public SQLTableSource() {

	}
	
	

	public int getIdType() {
		return idType;
	}

	public void setIdType(int idType) {
		this.idType = idType;
	}

	

	public TableDesc getTableDesc() {
		return tableDesc;
	}

	public void setTableDesc(TableDesc tableDesc) {
		this.tableDesc = tableDesc;
	}

	public Map<String, AssignID> getAssignIds() {
		return assignIds;
	}

	public void setAssignIds(Map<String, AssignID> assignIds) {
		this.assignIds = assignIds;
	}


	
}
