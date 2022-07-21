package org.beetl.sql.core.concat;

/**
 *
 *
 * @author xiandafu
 */
public class UpdateValueExpress extends Express {

    Update update;
    String varName;
    String col;
	String realVar;

    public UpdateValueExpress(Update update){
        this.update = update;
    }

    public UpdateValueExpress col(String colName){
        this.col = colName;
        return this;
    }
	public UpdateValueExpress real(String realVar){
		this.realVar = realVar;
		return this;
	}

    public Update tplValue(String varName){

        this.varName = varName;
        return update;
    }

    @Override
    public void toSql(ConcatBuilder sb) {
		if(realVar!=null){
			String col1 = sb.ctx.keyWordHandler.getCol(col);
			sb.append(col1).assign();
			sb.append(realVar);
		}else{
			//通常情况下
			String col1 = sb.ctx.keyWordHandler.getCol(col);
			sb.append(col1).assign();
			sb.appendVar(varName);
		}

    }
}
