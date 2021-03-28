package org.beetl.sql.core.nosql;

import org.beetl.sql.annotation.entity.*;
import org.beetl.sql.clazz.ColDesc;
import org.beetl.sql.clazz.TableDesc;
import org.beetl.sql.clazz.kit.BeanKit;
import org.beetl.sql.clazz.kit.JavaType;
import org.beetl.sql.clazz.kit.ThreadSafeCaseInsensitiveHashMap;
import org.beetl.sql.core.meta.MetadataManager;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.sql.Types;
import java.util.List;
import java.util.Set;

/**
 *
 * 对一些没有schema功能的数据库或者SQL查询引擎,可以通过class定义得到一个数据库schema
 * 比如数据来源一个json文件的。
 *
 * @author xiandafu
 * @see DrillStyle
 */
public class NoSchemaMetaDataManager implements MetadataManager {
    protected ThreadSafeCaseInsensitiveHashMap tableInfoMap =  new ThreadSafeCaseInsensitiveHashMap();;
    protected TableDesc NOT_EXIST = new TableDesc("$$$NOT_EXIST","");

    public NoSchemaMetaDataManager(){

    }

    /**
     * 从bean定义中获得schema，会分析bean中@Table和@Column,@AssignId等表和实体注解
     * @param beans
     */
    public NoSchemaMetaDataManager(List<Class> beans){
        beans.forEach(bean->parseBean(bean));
    }

    public void addBean(Class bean){
        parseBean(bean);
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
            this.tableInfoMap.put(tableDesc.getName(),tableDesc);
        } catch (IntrospectionException e) {
            e.printStackTrace();
        }

    }

    /**
     * 从java类型猜测数据类型，此方法没什么用处，仅仅是为了完善colDesc
     * @param colDesc
     * @param type
     */
    protected void initSqlType(ColDesc colDesc,Class type){
       Integer jdbcType =  JavaType.javaTypeJdbcs.get(type);
       if(jdbcType==null){
           if(type==Double.class||type==Float.class||type==double.class||type==float.class){
               //不建议类型未Double或者Float
               jdbcType = Types.NUMERIC;
           }
           //对于枚举或者复杂属性，暂时设置为Other
           jdbcType =  Types.OTHER;
       }
        colDesc.setSqlType(jdbcType);
        return ;
    }



    @Override
    public boolean existTable(String tableName) {
        return tableInfoMap.containsKey(tableName);
    }

    @Override
    public TableDesc getTable(String name) {
        return (TableDesc)tableInfoMap.get(name);
    }

    @Override
    public Set<String> allTable() {
        return tableInfoMap.keySet();
    }

    @Override
    public void addTableVirtual(String realTable, String virtual) {
        throw new UnsupportedOperationException("待完成");
    }
}
