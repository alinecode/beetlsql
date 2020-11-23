package org.beetl.sql.clazz;


import lombok.Data;
import org.beetl.sql.annotation.builder.AttributeConvert;
import org.beetl.sql.annotation.builder.BeanConvert;
import org.beetl.sql.annotation.builder.TargetAdditional;

import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.Map;

/**
 * 用户自定义的注解
 */
@Data
public class ClassExtAnnoation {
    private TargetAdditional targetAdditional;
    private Annotation additionalAnnotation;
    private Map<String, AttributeConvert>  attributeConvertMap ;
    private BeanConvert beanConvert;
    private Annotation beanConvertAnnotation;

   public boolean hasAttributeExt(){
       return attributeConvertMap!=null;
   }
   public boolean hasEntityExt(){
       return beanConvert!=null|| targetAdditional !=null;
   }



    public void addAttributeConvert(String attrName,AttributeConvert  convert){
        if(attributeConvertMap==null){
            attributeConvertMap = new HashMap<String, AttributeConvert>();
        }
        attributeConvertMap.put(attrName,convert);
    }




}
