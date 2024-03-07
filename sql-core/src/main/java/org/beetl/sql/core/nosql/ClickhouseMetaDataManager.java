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
import java.sql.Connection;

/**
 *
 * 支持metadata，但缺少部分metadata的，可以通过class定义得到一个数据库schema
 * 比如数据来源一个json文件的。
 *
 */
public class ClickhouseMetaDataManager extends SchemaLessMetaDataManager {


    public ClickhouseMetaDataManager(ConnectionSource ds, DBStyle style) {
        super(ds,style);
    }


    public void addBean(Class bean){
        parseBean(bean);
    }

    @Override
    protected  void moreInfo(Connection conn, TableDesc tableDesc){
        if(!this.extClassMap.containsKey(tableDesc.getName())){
            return ;
        }
        TableDesc classDesc = (TableDesc)extClassMap.get(tableDesc.getName());
        if(tableDesc.getIdNames().isEmpty()){
            //没有主键，使用class定义的主键
            classDesc.getIdNames().forEach(name->tableDesc.addIdName(name));
        }


    }

	/**
	 * 如果clickhouse 有主键，则使用。clickhouse作为分析库，一般没有主键（即使定义了主键，但clickhouse并不认为是主键)
	 * 通过此方法可以从Bean中添加主键定义
	 * @param bean
	 */
	protected void parseBean(Class bean){
    	String tableName = null;
        Table table = BeanKit.getAnnotation(bean,Table.class);
        if(table!=null){
            tableName = table.name();
        }
		TableDesc tableDesc = new TableDesc(tableName,null);
        try{
            PropertyDescriptor[] propertyDescriptors = BeanKit.propertyDescriptors(bean);
            for(PropertyDescriptor ps:propertyDescriptors){

                AssignID assignID =  BeanKit.getAnnotation(bean,ps.getName(), AssignID.class);
                if(assignID!=null){
					String colName = style.getNameConversion().getColName(bean,ps.getName());
					tableDesc.getIdNames().add(colName);
				}

            }
            this.extClassMap.put(tableDesc.getName(),tableDesc);
        } catch (IntrospectionException e) {
            e.printStackTrace();
        }

    }


}
