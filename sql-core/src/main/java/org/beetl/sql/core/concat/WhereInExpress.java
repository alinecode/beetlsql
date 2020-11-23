package org.beetl.sql.core.concat;

public class WhereInExpress extends Express {

    WhereNode where;
    String colName;
    String varName;
    String opt;

    public WhereInExpress(WhereNode where){
        this.where = where;
    }


    public void init( String opt, String name){
        this.opt = opt;
        this.colName = name;
    }


    public WhereNode tplValue(String varName){
        this.varName = varName;
        return where;
    }


    @Override
    public void toSql(ConcatBuilder sb) {

        sb.append(opt);
        String colName1 = sb.ctx.keyWordHandler.getCol(colName);
        sb.append(colName1);
        sb.append("in (");
        sb.appendVar("join("+varName+")");
        sb.append(")");

    }
}
