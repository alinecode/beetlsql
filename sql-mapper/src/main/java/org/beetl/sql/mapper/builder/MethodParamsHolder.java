package org.beetl.sql.mapper.builder;

import lombok.Data;

import java.util.List;

/**
 * 记录mapper方法的参数信息，如，参数名字
 */
@Data
public class MethodParamsHolder {
    List<MethodParam> paras = null;
    /**
     * 分页参数的索引
     */
    int pageRequestIndex = -1;
    /**
     * root变量的索引
     */
    int rootParamIndex = -1;

    public boolean hasPageRequest(){
        return pageRequestIndex!=-1;
    }

    public Object[] getArgsExcludePageRequest(Object[] args){
        if(!hasPageRequest()){
            return args;
        }
        Object[] newArgs =  new Object[args.length-1];
        System.arraycopy(args,0,newArgs,0,pageRequestIndex);
        if(pageRequestIndex==newArgs.length){
            return newArgs;
        }
        System.arraycopy(args,pageRequestIndex+1,newArgs,pageRequestIndex,args.length-pageRequestIndex);
        return  newArgs;
    }

}
