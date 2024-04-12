package org.beetl.sql.mapper.internal;

import org.beetl.core.util.ArrayUtils;
import org.beetl.sql.clazz.TableDesc;
import org.beetl.sql.clazz.kit.BeetlSQLException;
import org.beetl.sql.clazz.kit.ListUtil;
import org.beetl.sql.clazz.kit.StringKit;
import org.beetl.sql.core.SQLManager;
import org.beetl.sql.core.SQLReady;
import org.beetl.sql.core.query.LambdaQuery;
import org.beetl.sql.mapper.MapperInvoke;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Set;

/**
 * 根据主键将表字段设置为null
 *
 * @author liumin
 */
public class ClearFieldsByIdsAMI extends MapperInvoke {

    @Override
    @SuppressWarnings("unchecked")
    public Object call(SQLManager sm, Class entityClass, Method m, Object[] args) {
        if (!(args[0] instanceof List)) {
            throw new BeetlSQLException(BeetlSQLException.MAPPING_ERROR, "期望第一个参数是List");
        }
        List<Object> pks = (List<Object>)args[0];
        LambdaQuery.Property<Object, Object>[] props = (LambdaQuery.Property<Object, Object>[])args[1];
        if (ListUtil.isEmpty(pks) || ArrayUtils.isEmpty(props)) {
            throw new BeetlSQLException(BeetlSQLException.MAPPING_ERROR, "主键或要清空的字段不能为空");
        }
        String tableName = sm.getNc().getTableName(entityClass);
        if (StringKit.isEmpty(tableName)) {
            throw new BeetlSQLException(BeetlSQLException.MAPPING_ERROR, "根据Class找不到映射的数据库表");
        }
        LambdaQuery<Object> lambdaQuery = sm.lambdaQuery(entityClass);
        //数据库字段
        String[] cols = new String[props.length];
        for (int i = 0; i < props.length; i++) {
            cols[i] = lambdaQuery.getColumnName(props[i]);
        }
        //对主键分批次更新，防止IN过长报错,默认2000条一个批次,可以考虑改为参数化
        List<List<Object>> batchPks = ListUtil.partition(pks, 2000);
        batchPks.forEach(ids -> {
            clearTableCols(sm, entityClass, tableName, cols, ids);
        });
        return pks.size();
    }

    private boolean isLegalColumn(Set<String> cols, String col) {
        for (String column : cols) {
            if (column.equalsIgnoreCase(col)) {
                return true;
            }
        }
        return false;
    }

    private void clearTableCols(SQLManager sm, Class entityClass, String tableName, String[] columns, List<Object> pks) {
        TableDesc tableDesc = sm.getMetaDataManager().getTable(tableName);
        Set<String> pkNames = tableDesc.getIdNames();
        if (ListUtil.isEmpty(pkNames) || pkNames.size() != 1) {
            throw new BeetlSQLException(BeetlSQLException.MAPPING_ERROR, "表" + tableName + "没有主键或为复合主键，不支持清空字段操作");
        }
        LambdaQuery<?> query = sm.lambdaQuery(entityClass);
        query.andIn(pkNames.iterator().next(), pks);
        StringBuilder sql = new StringBuilder();
        sql.append("UPDATE ");
        sql.append(tableName.toUpperCase()).append(" SET ");
        List<String> col = ListUtil.newArrayList();
        for (String column : columns) {
            //检测列名是否合法
            if (!isLegalColumn(tableDesc.getCols(), column)) {
                continue;
            }
            column = column + "=NULL";
            column = column.toUpperCase();
            col.add(column);
        }
        if (ListUtil.isEmpty(col)) {
            throw new BeetlSQLException(BeetlSQLException.MAPPING_ERROR, "传入的表列名不合法");
        }
        sql.append(StringKit.join(col.iterator(), ",")).append(" ");
        sql.append(query.getSql());
        SQLReady sqlReady = new SQLReady(sql.toString(), pks.toArray());
        sm.executeUpdate(sqlReady);
    }

}
