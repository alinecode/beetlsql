package org.beetl.sql.core.concat;

/**
 *
 * @author xiandafu
 */
public class ColName extends Express {
    String col;
    public ColName(String col){
        this.col = col;
    }
    @Override
    public void toSql(ConcatBuilder sb) {
        String col1 = sb.getCtx().keyWordHandler.getCol(col);
        sb.append(col1);

    }
}
