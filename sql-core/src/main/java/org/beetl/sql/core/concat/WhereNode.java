package org.beetl.sql.core.concat;

import java.util.ArrayList;
import java.util.List;

public  class WhereNode   implements Output {
    ConcatContext ctx ;
    List<Express> whereList = new ArrayList<Express>();
    public WhereNode(ConcatContext ctx){
        this.ctx =ctx;
    }


    public WhereNode andEq(String name, String varName){
        WhereConditionExpress node = new WhereConditionExpress(this);
        node.init("=","and",name);
        node.tplValue(varName);
        whereList.add(node);
        return this;
    }

    public WhereNode andIn(String name, String varName){
        WhereInExpress node = new WhereInExpress(this);
        node.init("and",name);
        node.tplValue(varName);
        whereList.add(node);
        return this;
    }


    public WhereNode andIfNotEmpty(String name, String varName){
        WhereConditionExpress node = new WhereConditionExpress(this);
        node.init("=","and",name);
        node.tplValue(varName);
        NotEmptyExpress notEmptyExpress = new NotEmptyExpress(varName,node);
        whereList.add(notEmptyExpress);
        return this;
    }

    @Override
    public void toSql(ConcatBuilder sb) {
        if(whereList.isEmpty()){
            return ;
        }
        sb.append("where 1=1");
        for(Express express: whereList){
            express.toSql(sb);
        }

    }


}
