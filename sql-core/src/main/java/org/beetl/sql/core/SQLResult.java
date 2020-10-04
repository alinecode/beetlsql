package org.beetl.sql.core;

import org.beetl.sql.core.engine.SQLParameter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * SQL模板 执行结果
 * @author xiandafu
 */
public class SQLResult {

    /**
     * jdbc 对应的sql
     */
    public String jdbcSql;
    /**
     * jdbc对应的参数，包含了值，可能的对应的表达式
     */
    public List<SQLParameter> jdbcPara;


	public SQLResult(){

	}

	public SQLResult(String jdbcSql,Object[] para){
		this.jdbcSql = jdbcSql;
		this.jdbcPara  = toSQLParameters(para);
	}


	/**
	 * 获取jdbc对应的参数
	 * @return
	 */
	public Object[] toObjectArray(){
		if(jdbcPara==null){
			return new Object[0];
		}else {
			Object[] objs = new Object[jdbcPara.size()];
			int i =0;
			for(SQLParameter spa:jdbcPara) {
				objs[i++] = spa.value;
			}
			return objs;
		}
	}


	protected List<SQLParameter> toSQLParameters(Object[] args) {
		List<SQLParameter> paras = new ArrayList<SQLParameter>(args.length);
		for (Object arg : args) {
			paras.add(new SQLParameter(arg));
		}
		return paras;
	}
	
 }
