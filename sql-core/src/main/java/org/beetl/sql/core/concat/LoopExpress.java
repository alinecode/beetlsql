package org.beetl.sql.core.concat;

import java.sql.Array;
import java.util.ArrayList;
import java.util.List;

/**
 * 构造循环语句
 * @author xiandafu
 */
public class LoopExpress extends Express {

	String collection;
	String var;
	List<Express> list = new ArrayList<>();
	String opt ="and";
    public LoopExpress(String collection,String var){
        this.collection = collection;
        this.var = var;
    }

	public LoopExpress(String collection,String var,String opt){
		this.collection = collection;
		this.var = var;
		this.opt = opt;
	}

	/**
	 * ID=#{x.id}
	 * @param col
	 * @param varName
	 */
	public void addTpl(String col,String varName){
		WhereConditionExpress node = new WhereConditionExpress();
		node.init("=","and",col);
		node.tplValue(var+"."+varName);
		list.add(node);
	}


    @Override
    public void toSql(ConcatBuilder sb) {
		sb.append(opt);
		sb.appendTrimStart("or");
        sb.appendForStart(collection,var);
		sb.append("(");
		sb.append("1=1");
		for(Express express:list){

			express.toSql(sb);
		}
		sb.append(") or ");
		sb.appendTrimEnd();
        sb.appendForEnd();
    }
}
