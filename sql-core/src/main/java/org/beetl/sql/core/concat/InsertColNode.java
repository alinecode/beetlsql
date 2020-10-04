package org.beetl.sql.core.concat;

import java.util.ArrayList;
import java.util.List;

/**
 * @author xiandafu
 */
public class InsertColNode  extends  TrimSupport implements Output {
    List<Express> list = new ArrayList<>();
    Insert insert;
    public InsertColNode(Insert insert){
        this.insert = insert;
    }


    public InsertColNode add(String name){
        ColName colName = new ColName(name);
        list.add(colName);
        return this;
    }

    public InsertColNode conditional(String col, String varName){
        InsertColEmptyExpress notEmptyExpress = new InsertColEmptyExpress(col,varName);
        list.add(notEmptyExpress);
        super.trim = true;
        return this;
    }



    @Override
    public void toSql(ConcatBuilder sb) {
        if(trim){
            sb.appendTrimStart();
            for(int i=0;i<list.size();i++){
                Express express = list.get(i);
                express.toSql(sb);
            }
            sb.appendTrimEnd();

        }else{
            for(int i=0;i<list.size();i++){
                Express express = list.get(i);
                if(i!=0){
                    sb.comma();
                }
                express.toSql(sb);
            }

        }

    }

    public static class InsertColEmptyExpress extends  Express{
        String col;
        String varName;
        public InsertColEmptyExpress(String col,String varName){
            this.col = col;
            this.varName = varName;
        }
        @Override
        public void toSql(ConcatBuilder sb) {
            sb.testVar(varName,col);
        }
    }

}
