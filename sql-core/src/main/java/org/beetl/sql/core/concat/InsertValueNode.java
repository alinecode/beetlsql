package org.beetl.sql.core.concat;

import java.util.ArrayList;
import java.util.List;

/**
 * @author xiandafu
 */
public class InsertValueNode extends TrimSupport implements Output {
    static ValueHolderExpress holderExpress = new ValueHolderExpress();

    List<Express> list = new ArrayList<>();
    Insert insert;

    public InsertValueNode(Insert insert){
        this.insert = insert;
    }




    public void add(String varName){
        ValueExpress valueExpress = new ValueExpress(varName);
        list.add(valueExpress);
    }

    public void addConstants(String sql){
        ConstantExpress constantExpress = new ConstantExpress(sql);
        list.add(constantExpress);
    }

    public InsertValueNode conditional(String varName){
        InsertValueEmptyExpress valueExpress = new InsertValueEmptyExpress(varName);
        list.add(valueExpress);
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

    static class ValueExpress extends Express {
        String varName;
        public ValueExpress(String varName){
            this.varName = varName;
        }
        @Override
        public void toSql(ConcatBuilder sb) {
            sb.appendVar(varName);
        }
    }

    static  class ConstantExpress extends  Express{
        String sql ;
        public ConstantExpress(String sql){
            this.sql = sql;
        }

        @Override
        public void toSql(ConcatBuilder sb) {
            sb.append(sql);
        }
    }

    static class ValueHolderExpress extends Express {

        @Override
        public void toSql(ConcatBuilder sb) {
            sb.valueHolder();
        }
    }

    public static class InsertValueEmptyExpress extends  Express{
        String varName;
        public InsertValueEmptyExpress(String varName){
            this.varName = varName;
        }
        @Override
        public void toSql(ConcatBuilder sb) {
           sb.testVar(varName);
        }
    }


}
