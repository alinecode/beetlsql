package org.beetl.sql.mapper.builder;

import org.beetl.sql.clazz.kit.BeanKit;
import org.beetl.sql.core.ExecuteContext;
import org.beetl.sql.core.page.PageRequest;
import org.beetl.sql.mapper.annotation.Param;
import org.beetl.sql.mapper.annotation.Root;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public  class ParameterParser{
    Method method;
    MethodParamsHolder holder = null;
    public ParameterParser(Method method){
        this.method = method;
        holder = parse();
    }

    public MethodParamsHolder getHolder() {
        return holder;
    }

    protected MethodParamsHolder parse(){
        MethodParamsHolder holder = new MethodParamsHolder();
        List<MethodParam> paras = new ArrayList<>();
        Parameter[] paraConfig = method.getParameters();
        Class[] parasType = method.getParameterTypes();
        Annotation[][] anns = method.getParameterAnnotations();
        for (int i = 0; i < paraConfig.length; i++) {
            MethodParam para = new MethodParam();
            Param paramAnno = getParamAnnotation(anns[i]);
            boolean isRoot = isRoot(anns[i]);
            if(paramAnno!=null){
                para.setName(paramAnno.value());
            }else{
                para.setName(paraConfig[i].getName());
            }
            if(PageRequest.class.isAssignableFrom(parasType[i])){
                holder.setPageRequestIndex(i);
            }
            if(isRoot){
                para.setRoot(true);
                holder.setRootParamIndex(i);
            }
            para.setIndex(i);
            paras.add(para);
        }
        //如果方法还有一个参数
        if(parasType.length==1&&!BeanKit.isBaseDataType(parasType[0])
                &&!PageRequest.class.isAssignableFrom(parasType[0])){
            holder.setRootParamIndex(0);
            paras.get(0).setRoot(true);
        }
        holder.setParas(paras);
        return holder;
    }


    protected boolean isRoot(Annotation[] anns){
        boolean hasRoot = false;
        boolean hasParam = false;
        for(Annotation an:anns){
            if(an instanceof Root){
                hasRoot = true;
            }else if(an instanceof Param){
                hasParam = true;
            }
        }

        if(hasParam){
            return false;
        }else if(hasRoot){
            return true;
        }
        return false;
    }
    protected Param getParamAnnotation(Annotation[] anns){
        for(Annotation an:anns){
            if(an instanceof Param){
                return (Param)an;
            }
        }
        return null;
    }

    /**
     * 把mapper的方法的参数封装成Map传给sqlManager
     * @param paras
     * @param holder
     * @return
     */
    public static Object wrapParasForSQLManager(Object[] paras,MethodParamsHolder holder){
        if(paras.length==0){
            return new HashMap();
        }

        Map map = new HashMap();
        for(MethodParam param:holder.getParas()){
            map.put(param.getParamName(),paras[param.getIndex()]);
            if(param.isRoot()){
                map.put(ExecuteContext.ROOT_PARAM,paras[param.getIndex()]);
            }
        }
        return map;
    }
}
