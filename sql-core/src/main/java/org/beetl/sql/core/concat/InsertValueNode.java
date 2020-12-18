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
        ValueExpress valueExpress = new ValueExpress(this,varName);
        list.add(valueExpress);
    }

    public void addConstants(String sql){
        ConstantExpress constantExpress = new ConstantExpress(this,sql);
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
		InsertValueNode node;
        public ValueExpress(InsertValueNode node,String varName){
            this.varName = varName;
            this.node = node;
        }
        @Override
        public void toSql(ConcatBuilder sb) {
            sb.appendVar(varName);
			if(node.trim){
				sb.comma();
			}
        }
    }

    static  class ConstantExpress extends  Express{
        String sql ;
		InsertValueNode node;
        public ConstantExpress(InsertValueNode node,String sql){
			this.node = node;
            this.sql = sql;
        }

        @Override
        public void toSql(ConcatBuilder sb) {
            sb.append(sql);
            if(node.trim){
            	sb.comma();
			}
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
