package org.beetl.sql.core.annotatoin;

/**
 * 
 * 
 * @author zhoupan
 */
public enum SqlStatementType {

	AUTO, INSERT, UPDATE, SELECT, DELETE,BATCH_UPDATE;

	/**
	 * The Constructor.
	 */
	private SqlStatementType() {
	}
}
