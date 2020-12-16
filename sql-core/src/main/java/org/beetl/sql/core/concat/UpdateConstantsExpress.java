package org.beetl.sql.core.concat;

public class UpdateConstantsExpress extends Express {

    Update update;
    String col;
    Object value;

    public UpdateConstantsExpress(Update update){
        this.update = update;
    }


    public UpdateConstantsExpress col(String colName){
        this.col = colName;
        return this;
    }
    public Update value(Object var){
       this.value = var;
        return update;
    }



    @Override
    public void toSql(ConcatBuilder sb) {
        String col1 = sb.ctx.keyWordHandler.getCol(col);
        sb.append("set").append(col1).assign().append(value.toString());
    }
}
