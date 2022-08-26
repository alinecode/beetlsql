package org.beetl.sql.core.concat;

/**
 * @author xiandafu
 */
public class Insert implements Output {
    String mainTable;
    InsertColNode colNode;
    InsertValueNode valueNode;
    ConcatContext ctx;
    boolean trim = false;
    public Insert(ConcatContext ctx){
        this.ctx = ctx;
        colNode = new InsertColNode(this);
        valueNode = new InsertValueNode(this);
    }



    public Insert into(Class clazz){
        mainTable = ctx.keyWordHandler.getTable(ctx.nc.getTableName(clazz));
        return this;
    }


    public Insert set(String col, String varName){
        colNode.add(col);
        valueNode.add(varName);
        return this;
    }

	public Insert setWithReal(String col, String varName,String realVar){
		colNode.add(col);
		valueNode.add(varName,realVar);
		return this;
	}

    public Insert setConstant(String col, String sql){
        colNode.addConstants(col);
        valueNode.addConstants(sql);
        return this;
    }

    public Insert conditionalSet(String col, String varName){
        colNode.conditional(col,varName);
        valueNode.conditional(varName);
        return this;
    }


	public Insert conditionalSetWIthReal(String col, String varName,String realVar){
		colNode.conditional(col,varName);
		valueNode.conditionalWithReal(varName,realVar);
		return this;
	}


	public Insert conditionalSet(String col, String varName,String defaultValue){
		colNode.add(col);
		valueNode.conditional(varName,defaultValue);
		return this;
	}

    public String toSql(){
         ConcatBuilder sb = new ConcatBuilder(ctx);
         toSql(sb);
         return sb.toString();
    }

    //Override
    @Override
    public void toSql(ConcatBuilder sb) {
        sb.append("insert into");
		sb.append(mainTable);
        sb.leftBracket();
        colNode.toSql(sb);
        sb.rightBracket().append(" values").leftBracket();
        valueNode.toSql(sb);
        sb.rightBracket();
    }


}
