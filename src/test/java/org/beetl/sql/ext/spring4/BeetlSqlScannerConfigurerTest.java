package org.beetl.sql.ext.spring4;

import com.wing321.annotations.devtime.CreateTable;
import com.wing321.annotations.devtime.StoreData;
import com.wing321.test.PersistenceBaseTest;
import org.beetl.sql.ext.spring4.dao.DemoMapper;
import org.beetl.sql.ext.spring4.dao3.Demo3Mapper;
import org.beetl.sql.ext.spring4.dao2.Demo2Mapper;
import org.beetl.sql.test.User;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

/**
 * 对BeetSql整合Spring进行测试
 * @author woate
 */
@ContextConfiguration(locations = {"classpath*:org.beetl.sql.ext.spring4/testContext-spring.xml"})
@RunWith(SpringJUnit4ClassRunner.class)
public class BeetlSqlScannerConfigurerTest extends PersistenceBaseTest{
    @Autowired
    DemoMapper demoMapper;
    @Autowired
    Demo2Mapper demo2Mapper;
    @Autowired
    Demo3Mapper demo3Mapper;
    @Rollback(false)
    @Test
    @CreateTable(UserEntity.class)
    @StoreData(enable = true)
    public void testDemo(){
        List<UserEntity> list0 = demoMapper.all();
        Assert.assertEquals(0, list0.size());
        UserEntity userEntity = new UserEntity();
        userEntity.setSerialNo("1");
        userEntity.setName("1");
        userEntity.setAge(2);
        userEntity.setUserName("2");
        userEntity.setRoleId(3);
        demoMapper.insert(userEntity);
        List<UserEntity> list1 = demoMapper.all();
        Assert.assertEquals(1, list1.size());

        List<UserEntity> list2 = demoMapper.all();
        Assert.assertEquals(1, list2.size());

        List<UserEntity> list3 = demoMapper.all();
        Assert.assertEquals(1, list3.size());
//        //尝试使用DaoSupport支持类,这种需要自己实例化SqlManager
//        DemoMapper demoMapper1 = beetlSqlDaoSupport.getSqlManager().getMapper(DemoMapper.class);
//        List<User> list4 = demoMapper1.all();
//        Assert.assertEquals(1, list4.size());
    }
}