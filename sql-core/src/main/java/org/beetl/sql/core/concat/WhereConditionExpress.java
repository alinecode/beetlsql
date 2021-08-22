package org.beetl.sql.core.concat;

/**
 * where语句的表达式 比如 and name=#name#
 * @author xiandafu
 */
public class WhereConditionExpress extends Express {

    String colName;
    String varName;
    String cond;
    String opt;

    public WhereConditionExpress(){
    }

    public void init(String cond, String opt, String name){
        this.cond = cond;
        this.opt = opt;
        this.colName = name;
    }

    public void tplValue(String varName){
        this.varName = varName;
    }

    @Override
    public void toSql(ConcatBuilder sb) {
        String colName1 = sb.ctx.keyWordHandler.getCol(colName);
        sb.append(opt).append(colName1).append(cond);
        sb.appendVar(varName);
    }
}
