package org.beetl.sql.core.nosql;

import org.beetl.sql.annotation.entity.*;
import org.beetl.sql.clazz.ColDesc;
import org.beetl.sql.clazz.TableDesc;
import org.beetl.sql.clazz.kit.BeanKit;
import org.beetl.sql.clazz.kit.JavaType;
import org.beetl.sql.clazz.kit.ThreadSafeCaseInsensitiveHashMap;
import org.beetl.sql.core.ConnectionSource;
import org.beetl.sql.core.db.DBStyle;
import org.beetl.sql.core.meta.SchemaMetadataManager;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;

/**
 *
 * 支持metadata，但缺少部分metadata的，可以通过class定义得到一个数据库schema
 * 比如数据来源一个json文件的。
 *
 */
public class SchemaLessMetaDataManager extends SchemaMetadataManager {

    protected ThreadSafeCaseInsensitiveHashMap extClassMap = new ThreadSafeCaseInsensitiveHashMap();

    public SchemaLessMetaDataManager(ConnectionSource ds, DBStyle style) {
        super(ds,style);


    }

    public SchemaLessMetaDataManager(ConnectionSource ds, String defaultSchema, String defaultCatalog, DBStyle style) {
       super(ds,defaultSchema,defaultCatalog,style);

    }


    public void addBean(Class bean){
        parseBean(bean);
    }

    @Override
    protected  void moreInfo(TableDesc tableDesc){
        if(!this.extClassMap.containsKey(tableDesc.getName())){
            return ;
        }
        TableDesc classDesc = (TableDesc)extClassMap.get(tableDesc.getName());
        if(tableDesc.getIdNames().isEmpty()){
            //没有主键，使用class定义的主键
            classDesc.getIdNames().forEach(name->tableDesc.addIdName(name));
        }


    }

    protected void parseBean(Class bean){
        Table table = BeanKit.getAnnotation(bean,Table.class);
        if(table==null){
            throw new NullPointerException(bean+" 需要注解 @Table");
        }
        TableDesc tableDesc = new TableDesc(table.name(),bean.getName());
        try{
            PropertyDescriptor[] propertyDescriptors = BeanKit.propertyDescriptors(bean);
            for(PropertyDescriptor ps:propertyDescriptors){
                Column column = BeanKit.getAnnotation(bean,ps.getName(), Column.class);
                if(column==null){
                    continue;
                }
                String colName = column.value();
                Class type = ps.getPropertyType();
                ColDesc colDesc = new ColDesc(colName);
                initSqlType(colDesc,type);
                tableDesc.addCols(colDesc);

                AssignID assignID =  BeanKit.getAnnotation(bean,ps.getName(), AssignID.class);
                if(assignID!=null){
                    tableDesc.addIdName(column.value());
                    continue;
                }

                AutoID autoID =  BeanKit.getAnnotation(bean,ps.getName(), AutoID.class);
                if(autoID!=null){
                    tableDesc.addIdName(column.value());
                    continue;
                }

                SeqID seqID =  BeanKit.getAnnotation(bean,ps.getName(), SeqID.class);
                if(seqID!=null){
                    tableDesc.addIdName(column.value());
                    continue;
                }


            }
            this.extClassMap.put(tableDesc.getName(),tableDesc);
        } catch (IntrospectionException e) {
            e.printStackTrace();
        }

    }

    protected void initSqlType(ColDesc colDesc,Class type){
       Integer jdbcType =  JavaType.javaTypeJdbcs.get(type);
       if(jdbcType==null){
           throw new IllegalArgumentException("NoSchemaMetaDataManager 不支持类型 "+type);
       }
        colDesc.setSqlType(jdbcType);
       //不设置size应该不会有问题，beetlsql几乎没用上这个，除非以后增加从Pojo生成create table语句，这个才有意义
        return ;

    }


}
