package org.beetl.sql.core.concat;

/**
 * where语句的表达式 比如 and name=#name#
 * @author xiandafu
 */
public class WhereConstConditionExpress extends WhereConditionExpress {


    public WhereConstConditionExpress(){
    }

    public void constValue(String value){
        this.varName = value;
    }

    @Override
    public void toSql(ConcatBuilder sb) {
        String colName1 = sb.ctx.keyWordHandler.getCol(colName);
        sb.append(opt).append(colName1).append(cond);
        sb.append(varName);
    }
}
