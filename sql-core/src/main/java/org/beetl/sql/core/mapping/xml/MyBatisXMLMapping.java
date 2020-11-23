package org.beetl.sql.core.mapping.xml;

import org.beetl.sql.core.ExecuteContext;
import org.beetl.sql.core.mapping.ResultSetMapper;

import java.lang.annotation.Annotation;
import java.sql.ResultSet;
import java.util.List;

/**
 * 模仿mybatis的映射风格,根据target类型从classpath中找到特定定映射xml文件配置，根据配置进行映射
 * 目前支持简单xml映射，不支持复杂的，未来可以扩展
 *  <pre>{@
 *       @ResultSetMappingProvider(MyBatisXMLMapping.class)
 *       public class User{
 *  		List<Role> roles
 *       }
 *   }</pre>
 *
 * @author xiandafu
 * @since 3.0
 */
public class MyBatisXMLMapping implements ResultSetMapper {



    @Override
    public List mapping(ExecuteContext ctx, Class target, ResultSet resultSet, Annotation config) {
        throw new UnsupportedOperationException("不支持，期待熟悉mybats映射的人能完成");
    }
}
