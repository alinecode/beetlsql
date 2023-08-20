package org.beetl.sql.test.mappping;

import org.beetl.sql.clazz.NameConversion;
import org.beetl.sql.clazz.kit.BeanKit;
import org.beetl.sql.clazz.kit.CaseInsensitiveHashMap;
import org.beetl.sql.clazz.kit.PropertyDescriptorWrap;
import org.beetl.sql.core.ExecuteContext;
import org.beetl.sql.core.mapping.join.AttrNode;
import org.beetl.sql.core.mapping.join.ConfigJoinMapper;
import org.beetl.sql.core.mapping.join.JsonConfigMapper;
import org.beetl.sql.test.annotation.XmlMapping;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;

import java.beans.PropertyDescriptor;
import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.sql.ResultSetMetaData;
import java.util.HashMap;
import java.util.Map;

/**
 * 一个简单演示，如何使用xml配置映射,参考user.xml
 * @see XmlMapping
 * @see JsonConfigMapper
 * @author xiandafu
 */
public class SimpleXMLMapping extends ConfigJoinMapper {


    @Override
    protected AttrNode parse(ExecuteContext ctx, Class target, ResultSetMetaData rsmd, Annotation config) throws Exception {
        NameConversion nc = ctx.sqlManager.getNc();

        AttrNode root = new AttrNode();
        root.target =target;
        root.parent = null;
        root.typePdInParent = null;
        Map<Integer,String> colMap = new HashMap<>();
        Map<String,PropertyDescriptorWrap> propertyMap = new HashMap<>();

        XmlMapping mapping = (XmlMapping)config;
        String path = mapping.path();
        SAXBuilder builder = new SAXBuilder();
        InputStream file = ctx.sqlManager.getClassLoaderKit().loadResource(path);
        if(file==null){
            throw new IllegalArgumentException("配置文件不存在" +file);
        }
        try {
            Document document = builder.build(file);
            Element element = document.getRootElement();
            Map<String,String> colAttrMap = mappingConfig(element);
            int count = rsmd.getColumnCount();
            for(int i=1;i<=count;i++){
                String colName= rsmd.getColumnName(i);
                String attrName = colAttrMap.get(colName);
                if(attrName==null){
                    continue;
                }

				PropertyDescriptorWrap pd = BeanKit.getPropertyDescriptorWrap(target,attrName);
                propertyMap.put(attrName,pd);
                colMap.put(i,attrName);
            }

            root.colMap = colMap;
            root.propertyMap = propertyMap;
            return root;

        } catch (JDOMException e) {
            throw new IllegalStateException(e);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    protected Map<String,String> mappingConfig(Element element){
        Map<String,String> colAttrMap = new CaseInsensitiveHashMap();
        element.getChildren().forEach(ele->{
            String attr = ele.getName();
            String col = ele.getText().trim();
            colAttrMap.put(col,attr);
        });
        return colAttrMap;


    }
}
