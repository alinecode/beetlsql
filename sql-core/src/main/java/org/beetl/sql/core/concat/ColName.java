package org.beetl.sql.core.concat;

/**
 *
 * @author xiandafu
 */
public class ColName extends Express {
    String col;
	InsertColNode colNode;
    public ColName(String col){
    	this.colNode = colNode;
        this.col = col;
    }
    @Override
    public void toSql(ConcatBuilder sb) {
        String col1 = sb.getCtx().keyWordHandler.getCol(col);
        sb.append(col1);
    }
}
