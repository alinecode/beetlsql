package org.beetl.sql.core.call;

import lombok.Data;

import org.beetl.sql.core.SqlId;

import java.util.ArrayList;
import java.util.List;

@Data
public class CallReady {
	String sql;
	int index ;
	SqlId sqlId = null;
	List<CallArg> args = new ArrayList();

	/**
	 * 构造一个无出参，入参的存储过程
	 * @param sql 调用存储过程语句
	 */
	public CallReady(String sql){
		this.sql = sql;
		this.index = 1;
	}

	/**
	 *  构造一个自有入的存储过程
	 * @param sql  存错过程
	 * @param args 入参，如果想设置出参数，则需要调用add 方法
	 */
	public CallReady(String sql,Object... args){
		this.sql = sql;
		this.index = 1;
		for(Object o:args){
			add(new InArg(o));
		}

	}

	/**
	 * 构造一个自有入参的存储过程
	 * @param sqlId
	 * @param sql
	 * @param args
	 */
	public CallReady(SqlId sqlId,String sql,Object... args){
		this.sql = sql;
		this.index = 1;
		this.sqlId  = sqlId;
		for(Object o:args){
			add(new InArg(o));
		}
	}


	public CallReady add(CallArg callArg){
		args.add(callArg);
		callArg.setIndex(index);
		index++;
		return this;
	}

	public Object getOutValue(int index){
		CallArg callArg =  args.get(index-1);
		if(!(callArg instanceof OutArg) ){
			throw new IllegalArgumentException("需要指定一个Out索引 "+index);
		}
		return ((OutArg)callArg).getOutValue();
	}


}

