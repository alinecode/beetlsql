package org.beetl.sql.util;

import lombok.Data;
import org.beetl.sql.annotation.entity.*;
import org.beetl.sql.clazz.kit.BeanKit;
import org.beetl.sql.mapper.BaseMapper;
import org.junit.Assert;
import org.junit.Test;

import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.sql.Date;
import java.util.List;
import java.util.Map;

/**
 * 测试BeanKit方法
 * @author xiandafu
 */
public class BeanKitTest {

    @Test
    public void testFindAnnotation(){
        Class target = OrderEntity.class;

        Table table =  BeanKit.getAnnotation(target, Table.class);
        Assert.assertNotNull(table);

        AssignID assignID =  BeanKit.getAnnotation(target,"id", AssignID.class);
        LogicDelete delete =  BeanKit.getAnnotation(target,"delete", LogicDelete.class);
        Assert.assertNotNull(assignID);
        Assert.assertNotNull(delete);

        UpdateIgnore ui =  BeanKit.getAnnotation(target,"createTime", UpdateIgnore.class);
        InsertIgnore ii =  BeanKit.getAnnotation(target,"updateTime", InsertIgnore.class);
        Assert.assertNotNull(ui);
        Assert.assertNotNull(ii);
    }


    @Test
    public void testFiled(){
        OrderEntity orderEntity = new OrderEntity();
        BeanKit.setBeanProperty(orderEntity,1,"id");
        Assert.assertEquals(orderEntity.getId().intValue(),1);
        Assert.assertEquals(orderEntity.getId().intValue()
                ,BeanKit.getBeanProperty(orderEntity,"id"));
        Long l =2l;
        BeanKit.setBeanPropertyWithCast(orderEntity,l,"id");
        Assert.assertEquals(orderEntity.getId().intValue(),2);

        BigDecimal big = new BigDecimal("2");
        BeanKit.setBeanPropertyWithCast(orderEntity,big,"id");
        Assert.assertEquals(orderEntity.getId().intValue(),2);

    }

    @Test
    public void testInstance(){
        OrderEntity order = BeanKit.newSingleInstance(OrderEntity.class);
        OrderEntity order2 = BeanKit.newSingleInstance(OrderEntity.class);
        Assert.assertEquals(order,order2);

    }

    @Test
    public void testParameterType() throws Exception{
       Class cls = Mapper.class;
       Method method1= cls.getMethod("query1",new Class[0]);
       Class type1 = BeanKit.getCollectionType(method1.getGenericReturnType());
       Assert.assertNull(type1);

        Method method2 = cls.getMethod("query2",new Class[0]);
        Class type2 = BeanKit.getCollectionType(method2.getGenericReturnType());
        Assert.assertTrue(type2==OrderEntity.class);



        Method method3 = cls.getMethod("query3",new Class[0]);
        Class type3 = BeanKit.getCollectionType(method3.getGenericReturnType());
        Assert.assertTrue(type3==Map.class);


        Method method4 = cls.getMethod("query4",new Class[0]);
        Class type4 = BeanKit.getCollectionType(method4.getGenericReturnType());
        Assert.assertTrue(type4==Map.class);

    }


    @Test
    public void testMapperInterface() throws Exception{
        Class cls = Mapper.class;
        Class c = BeanKit.getMapperEntity(cls);
        Assert.assertTrue(c==null);

        Class cls2 = Mapper2.class;
        Class c2 = BeanKit.getMapperEntity(cls2);
        Assert.assertTrue(c2==OrderEntity.class);

    }

    @Table(name="order")
    @Data
    public static class OrderEntity extends BaseEntity{
        @AssignID
        private Integer id;
        @UpdateIgnore
        private Date createTime;
        @InsertIgnore
        private Date updateTime;
    }



    @Data
    public static class BaseEntity{
        @LogicDelete
        private int delete;
    }


    public interface  Mapper{
        public List query1();
        public List<OrderEntity> query2();
        public List<Map> query3();
        public List<Map<String,Object>> query4();
    }

    public interface  Mapper2 extends BaseMapper<OrderEntity> {

    }

}
