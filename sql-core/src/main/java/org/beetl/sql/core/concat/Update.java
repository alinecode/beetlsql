package org.beetl.sql.core.concat;

import java.util.ArrayList;
import java.util.List;

/**
 * @author xiandafu
 */
public class Update extends WhereNode {
    String mainTable;
    boolean trim;
    List<Express> updateList = new ArrayList<Express>();
    public Update(ConcatContext ctx) {
        super(ctx);
    }

    public Update from(Class target) {
        mainTable = ctx.keyWordHandler.getTable(ctx.nc.getTableName(target));
        return this;
    }


    public Update table(String table) {
        this.mainTable = table;
        return this;
    }

    public UpdateValueExpress assign(String col){
        UpdateValueExpress updateValueExpress = new UpdateValueExpress(this);
        updateValueExpress.col = col;
        updateList.add(updateValueExpress);
        return updateValueExpress;
    }



    public Update assignConstants(String col,Object value){
        UpdateConstantsExpress updateValueExpress = new UpdateConstantsExpress(this);
        updateValueExpress.col = col;
        updateValueExpress.value(value);
        updateList.add(updateValueExpress);
        return this;
    }

	public Update assignVersion(String col){
		UpdateVersionExpress updateValueExpress = new UpdateVersionExpress(this);
		updateValueExpress.col = col;
		updateList.add(updateValueExpress);
		return this;
	}



    public Update notEmptyAssign(String varName, String col){
        UpdateValueExpress updateValueExpress = new UpdateValueExpress(this).col(col);
        updateValueExpress.tplValue(varName);
        UpdateNotEmptyExpress notEmptyExpress = new UpdateNotEmptyExpress(varName, updateValueExpress);
        updateList.add(notEmptyExpress);
        trim = true;
        return this;
    }

	public Update notEmptyAssign(String varName, String col,String realVar){
		UpdateValueExpress updateValueExpress = new UpdateValueExpress(this).col(col).real(realVar);
		updateValueExpress.tplValue(varName);
		UpdateNotEmptyExpress notEmptyExpress = new UpdateNotEmptyExpress(realVar, updateValueExpress);
		updateList.add(notEmptyExpress);
		trim = true;
		return this;
	}


    @Override
    public void toSql(ConcatBuilder sb) {
        sb.append("update");
        sb.append(mainTable).append("set");

        if(trim){
            sb.appendTrimStart();
            for(int i=0;i<updateList.size();i++){
				Express express = (Express)updateList.get(i);
                express.toSql(sb);
            }
            sb.appendTrimEnd();
        }else{
            for(int i=0;i<updateList.size();i++){
                Express express = updateList.get(i);
                if(i!=0){
                    sb.append(",");
                }
                express.toSql(sb);
            }
        }

        super.toSql(sb);

    }

    public String toSql() {
        ConcatBuilder sb = ctx.concatBuilder;
        this.toSql(sb);
        return sb.toString();
    }
}
