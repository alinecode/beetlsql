package org.beetl.sql.core;

import lombok.Data;
import org.beetl.sql.annotation.entity.Column;
import org.beetl.sql.annotation.entity.Table;
import org.junit.Assert;
import org.junit.Test;

/**
 * 测试内置的命令转化
 * @author xiandafu
 */
public class NameConversionTest {

    @Test
    public void testUnder(){
        UnderlinedNameConversion conversion = new UnderlinedNameConversion();
        String tableName = conversion.getTableName(TestObject.class);
        Assert.assertEquals("test_object",tableName);
        String col = conversion.getColName(TestObject.class,"userName");
        Assert.assertEquals("user_name",col);

    }


    @Test
    public void testDefault(){
        DefaultNameConversion conversion = new DefaultNameConversion();
        String tableName = conversion.getTableName(TestObject.class);
        Assert.assertEquals("TestObject",tableName);
        String col = conversion.getColName(TestObject.class,"userName");
        Assert.assertEquals("userName",col);

    }

    @Test
    public void testAnnotation(){

        UnderlinedNameConversion conversion = new UnderlinedNameConversion();
        String tableName = conversion.getTableName(TestObject2.class);
        Assert.assertEquals("test_object",tableName);
        String col = conversion.getColName(TestObject2.class,"name");
        Assert.assertEquals("user_name",col);

    }

    @Data
    public static class TestObject{
        private Integer id;
        private String userName;

    }

    @Table(name="test_object")
    @Data
    public static class TestObject2{
        private Integer id;
        @Column("user_name")
        private String name;

    }
}



