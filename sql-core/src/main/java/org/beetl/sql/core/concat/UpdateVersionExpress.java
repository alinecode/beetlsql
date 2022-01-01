package org.beetl.sql.core.concat;

public class UpdateVersionExpress extends Express {

    Update update;
    String col;
    Object value;

    public UpdateVersionExpress(Update update){
        this.update = update;
    }

    public UpdateVersionExpress col(String colName){
        this.col = colName;
        return this;
    }


    @Override
    public void toSql(ConcatBuilder sb) {
        String col1 = sb.ctx.keyWordHandler.getCol(col);
		if(update.trim){
			//牵强代码，如果是templateUpdate，则需要补充","
			sb.comma();
		}
        sb.append(col1).assign().append(col1).append("+1");

    }
}
