package org.beetl.sql.core;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class CallReady {
	String sql;
	int index ;
	SqlId sqlId = null;
	List<CallArg> args = new ArrayList();;
	public CallReady(String sql){
		this.sql = sql;
		this.index = 1;
	}

	public CallReady(SqlId sqlId,String sql){
		this.sql = sql;
		this.index = 1;
		this.sqlId  = sqlId;
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


	@Data
	public static class CallArg{
		private int index;
		protected int jdbcType = Integer.MAX_VALUE;

		public boolean hasJdbcType(){
			return jdbcType!= Integer.MAX_VALUE;
		}
	}

	@Data
	public static class InArg extends  CallArg{
		private Object arg;
		public InArg(Object arg){
			this.arg = arg;
		}

		public InArg(Object arg,int jdbcType){
			this.arg = arg;
			this.jdbcType = jdbcType;
		}
	}

	@Data
	public static class OutArg extends  CallArg{
		private Class outType;
		private Object outValue;
		public OutArg(Class ouyType){
			this.outType = ouyType;
		}
		public OutArg(int jdbcType){
			this.jdbcType = jdbcType;
		}

	}


}

