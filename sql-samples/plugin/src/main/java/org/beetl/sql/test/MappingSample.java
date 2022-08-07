package org.beetl.sql.test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.beetl.sql.annotation.entity.Auto;
import org.beetl.sql.annotation.entity.ResultProvider;
import org.beetl.sql.annotation.entity.RowProvider;
import org.beetl.sql.annotation.entity.Table;
import org.beetl.sql.core.ExecuteContext;
import org.beetl.sql.core.SQLManager;
import org.beetl.sql.core.SQLReady;
import org.beetl.sql.core.mapping.BeanProcessor;
import org.beetl.sql.core.mapping.RowMapper;
import org.beetl.sql.core.mapping.type.JavaSqlTypeHandler;
import org.beetl.sql.core.mapping.type.ReadTypeParameter;
import org.beetl.sql.core.mapping.type.WriteTypeParameter;
import org.beetl.sql.sample.SampleHelper;
import org.beetl.sql.test.annotation.XmlMapping;
import org.beetl.sql.test.mappping.SimpleXMLMapping;

import java.lang.annotation.Annotation;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * 演示如实如何实现TypeHandler转化BigInteger，和JsonNode，以及RowMapper，xml定义的ResultSetMapper，这些提供了灵活的映射
 */
public class MappingSample {
    SQLManager sqlManager;

    public MappingSample(SQLManager sqlManager) {
        this.sqlManager = sqlManager;

    }

    public static void main(String[] args) throws Exception {
        SQLManager sqlManager = SampleHelper.getSqlManager();
        MappingSample mappingSample = new MappingSample(sqlManager);
        mappingSample.rowMapper();
        mappingSample.typeHandler4BigInt();
        mappingSample.typeHandler4JacksonNode();
        mappingSample.xmlMapping();
    }

    /**
     * 把查询结果集额外的列映射到属性上
     */
    public void rowMapper(){
        String sql = "select u.*,'[1,2]' col from sys_user u where id=1 ";
        UserVo info = sqlManager.execute(new SQLReady(sql), UserVo.class).get(0);
        System.out.println(info.getExtraAttribute());

        info = sqlManager.rowMapper(MyRowMapper.class).execute(new SQLReady(sql), UserVo.class).get(0);
        System.out.println(info.getExtraAttribute());
        //更常见的方式是通过注解RowProvider完成
        info = sqlManager.execute(new SQLReady(sql), UserVo2.class).get(0);
        System.out.println(info.getExtraAttribute());
    }

    /**
     * 实现BigInteger转化
     */
    public void typeHandler4BigInt(){

        BigIntTypeHandler bigIntTypeHandler = new BigIntTypeHandler();
        sqlManager.getDefaultBeanProcessors().addHandler(BigInteger.class,bigIntTypeHandler);

        User2 user = new User2();
        user.setName("lll");
        user.setDepartmentId(new BigInteger("1"));
        sqlManager.insert(user);

        Integer id = user.getId();
        User2 dbUser = sqlManager.unique(User2.class,1);
        System.out.println(id+":"+dbUser.getDepartmentId());

    }

    /**
     * 实现jackson node 转化
     */
    public void typeHandler4JacksonNode(){

        JsonNodeTypeHandler typeHandler = new JsonNodeTypeHandler();
        /*注册需要处理的所有JsonNode子类*/
        sqlManager.getDefaultBeanProcessors().addAcceptType(
                new BeanProcessor.InheritedAcceptType(
                        JsonNode.class,typeHandler));

        UserNode user = new UserNode();
        JsonNode node = JsonNodeTypeHandler.objectMapper.createObjectNode().textNode("abcefg");
        user.setName(node);
        sqlManager.insert(user);

        Integer id = user.getId();
        UserNode dbUser = sqlManager.unique(UserNode.class,1);
        System.out.println(id+":"+dbUser.getName());

    }

    /**
     * 按照xml定义映射，SimpleXMLMapping实现了ResultSetMapper接口
     */
    public void xmlMapping(){
        User3 user = sqlManager.unique(User3.class,1);
        System.out.println(user.getUserName());
    }






    @Data
    public static class UserVo {
        Integer id;
        String name;
        String[] extraAttribute;

    }
    @Data
    @EqualsAndHashCode(callSuper = false)
    @RowProvider(MyRowMapper.class)
    public static class UserVo2 extends UserVo{

    }

    /**
     * 把jsonNode存放到数据库，或者读取出来，在PluginAnnotation演示了通过自定义注解Jackson实现
     * 这次我们可以换一个更底层的，实现一个类型处理器
     */
    @Table(name="sys_user")
    @Data
    public static class UserNode{
        @Auto
        Integer id;
        JsonNode name;
    }

    @Table(name="sys_user")
    @Data
    public static class User2{
        @Auto
        Integer id;
        String name;
        BigInteger departmentId;
    }


    /**
     * 使用xml配置映射规则,参考user.xml,ResultProvider表示如何映射，XmlMapping是映射配置
     */
    @Table(name="sys_user")
    @Data
    @ResultProvider(SimpleXMLMapping.class) //映射类
    @XmlMapping(path= "userMapping.xml") //映射类参数
    public static class User3{
        Integer id;
        String userName;
        Integer deptId;
    }

    /**
     * Jackson的JsonNode类型处理类,使得java属性可以是JsonNode。
     * 另外一种更通用的是PluginAnnotation例子中的@Jackson注解
     */
    public static class JsonNodeTypeHandler extends JavaSqlTypeHandler{
        static ObjectMapper objectMapper = new ObjectMapper();
        @Override
        public Object getValue(ReadTypeParameter typePara) throws SQLException {
            String str = typePara.getRs().getString(typePara.getIndex());
            JsonNode node = objectMapper.valueToTree(str);
            return node;
        }
        @Override
        public void setParameter(WriteTypeParameter writeTypeParameter, Object obj)throws SQLException {
            JsonNode node = (JsonNode)obj;
            try {
                String text = objectMapper.writeValueAsString(node);
                writeTypeParameter.getPs().setString(writeTypeParameter.getIndex(),text);
            } catch (JsonProcessingException e) {
                throw new IllegalArgumentException("不是json格式");
            }

        }
    }

    /**
     * Beetlsql 并没有内置对BigInteger支持，这里可以扩展
     */
    public static class BigIntTypeHandler extends JavaSqlTypeHandler{
        @Override
        public Object getValue(ReadTypeParameter typePara) throws SQLException {
            BigDecimal decimal = typePara.getRs().getBigDecimal(typePara.getIndex());
            return decimal.toBigInteger();

        }
        @Override
        public void setParameter(WriteTypeParameter writeTypeParameter, Object obj)throws SQLException {
            BigInteger bigInteger = (BigInteger)obj;
            BigDecimal bigDecimal = new BigDecimal(bigInteger);
            writeTypeParameter.getPs().setBigDecimal(writeTypeParameter.getIndex(),bigDecimal);
        }
    }

    /**
     * 自定义一个行映射
     */
    public static class MyRowMapper implements RowMapper<UserVo>{

        @Override
        public UserVo mapRow(ExecuteContext ctx, Object obj, ResultSet rs, int rowNum, Annotation config) throws SQLException {
            //内置的映射已经完成
            UserVo vo = (UserVo)obj;
            //额外取得结果集
            String col = rs.getString("col");
            String[] arrays = col.split(",");
            vo.setExtraAttribute(arrays);
            return vo;
        }
    }
}
