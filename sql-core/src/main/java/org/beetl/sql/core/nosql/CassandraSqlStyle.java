package org.beetl.sql.core.nosql;

import org.beetl.sql.clazz.kit.BeanKit;
import org.beetl.sql.core.db.AbstractDBStyle;
import org.beetl.sql.core.db.DBType;
import org.beetl.sql.core.range.RangeSql;

import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Map;

/**
 * 注意，CassandraSqlStyle不支持自动生成翻页
 *
 * @author xiandafu
 */
public class CassandraSqlStyle extends AbstractDBStyle {

    CassandraRangeSql cassandraRangeSql = null;

    public CassandraSqlStyle() {
        super();
        cassandraRangeSql = new CassandraRangeSql(this);
    }


    @Override
    public int getIdType(Class c, String idProperty) {
        List<Annotation> ans = BeanKit.getAllAnnotation(c, idProperty);
        int idType = DBType.ID_ASSIGN; //默认是自增长
        return idType;
    }

    @Override
    public String getName() {
        return "cassandra";
    }

    @Override
    public int getDBType() {
        return DBType.DB_CASSANDRA;
    }

    @Override
    public RangeSql getRangeSql() {
        return cassandraRangeSql;
    }

    /**
     *  Cassandra 翻页不支持，然而，开发者可以根据自己项目特点实现自动翻页语句
     *
     */
    public static class CassandraRangeSql implements RangeSql {
        AbstractDBStyle sqlStyle = null;

        public CassandraRangeSql(AbstractDBStyle style) {
            this.sqlStyle = style;
        }

        @Override
        public String toRange(String jdbcSql, Object objOffset, Long limit) {
            throw new UnsupportedOperationException("cassandra 翻页查询需要手工sql完成，无法辅助自动生成");
        }

        @Override
        public String toTemplateRange(Class mapping, String template) {
            //do nothing,开发者自己完成提供翻页语句
            return template;
        }

        @Override
        public void addTemplateRangeParas(Map<String, Object> paras, Object offset, long size) {
            //do nothing
        }
    }


}
