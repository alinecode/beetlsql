package org.beetl.sql.ext.spring4;

import org.beetl.sql.ext.spring4.dao.DemoMapper;
import org.beetl.sql.ext.spring4.dao3.Demo3Mapper;
import org.beetl.sql.ext.spring4.dao2.Demo2Mapper;
import org.beetl.sql.test.User;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

/**
 * 对BeetSql整合Spring进行测试
 * @author woate
 */
@ContextConfiguration(locations = {"classpath*:org.beetl.sql.ext.spring4/testContext-spring.xml"})
@RunWith(SpringJUnit4ClassRunner.class)
public class BeetlSqlScannerConfigurerTest{
    @Autowired
    DemoMapper demoMapper;
    @Autowired
    Demo2Mapper demo2Mapper;
    @Autowired
    Demo3Mapper demo3Mapper;
    @Autowired
    JdbcTemplate jdbcTemplate;
    @Test
    public void testDemo(){
        jdbcTemplate.execute("CREATE TABLE user (\n" +
                "  id int(11) NOT NULL AUTO_INCREMENT,\n" +
                "  name varchar(64) DEFAULT NULL,\n" +
                "  age int(4) DEFAULT NULL,\n" +
                "  userName varchar(64) DEFAULT NULL,\n" +
                "  roleId int(11) DEFAULT NULL,\n" +
                "  create_time datetime DEFAULT NULL,\n" +
                "  PRIMARY KEY (id)\n" +
                ") ");
        List<User> list0 = demoMapper.all();
        Assert.assertEquals(0, list0.size());
        jdbcTemplate.execute("insert into user(id,name,age,userName,roleId) VALUES (1,'1',2,'2',3)");
        List<User> list1 = demoMapper.all();
        Assert.assertEquals(1, list1.size());

        List<User> list2 = demoMapper.all();
        Assert.assertEquals(1, list2.size());

        List<User> list3 = demoMapper.all();
        Assert.assertEquals(1, list3.size());
//        //尝试使用DaoSupport支持类,这种需要自己实例化SqlManager
//        DemoMapper demoMapper1 = beetlSqlDaoSupport.getSqlManager().getMapper(DemoMapper.class);
//        List<User> list4 = demoMapper1.all();
//        Assert.assertEquals(1, list4.size());
    }
}