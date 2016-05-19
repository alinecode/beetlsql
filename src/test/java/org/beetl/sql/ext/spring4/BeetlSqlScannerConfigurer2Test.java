package org.beetl.sql.ext.spring4;

import java.util.List;

import org.beetl.sql.ext.spring4.dao.DemoDao;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.wing321.annotations.devtime.CreateTable;
import com.wing321.annotations.devtime.StoreData;
import com.wing321.test.PersistenceBaseTest;

import junit.framework.Assert;

/**
 * 对BeetSql整合Spring进行测试,逐步使用win框架  http://git.oschina.net/woate/Wing
 * 这个测试Spring 标签配置整合
 * @author woate
 */
@ContextConfiguration(locations = {"classpath*:org.beetl.sql.ext.spring4/testContext-spring2.xml"})
@RunWith(SpringJUnit4ClassRunner.class)
public class BeetlSqlScannerConfigurer2Test extends PersistenceBaseTest{
    @Autowired
    DemoDao dao;
    @Rollback(false)
    @Test
    @CreateTable(UserEntity.class)
    @StoreData(enable = true)
    public void testDemo(){
        List<UserEntity> list0 = dao.all();
        Assert.assertEquals(0, list0.size());
        UserEntity userEntity = new UserEntity();
        userEntity.setName("1");
        userEntity.setAge(2);
        userEntity.setUserName("2");
        userEntity.setRoleId(3);
        dao.insert(userEntity);
        List<UserEntity> list1 = dao.all();
        Assert.assertEquals(1, list1.size());

        list1 = dao.queryAll();
        Assert.assertEquals(1, list1.size());

        List<UserEntity> list2 = dao.all();
        Assert.assertEquals(1, list2.size());

        List<UserEntity> list3 = dao.all();
        Assert.assertEquals(1, list3.size());
//        //尝试使用DaoSupport支持类,这种需要自己实例化SqlManager
//        DemoMapper demoMapper1 = beetlSqlDaoSupport.getSqlManager().getMapper(DemoMapper.class);
//        List<User> list4 = demoMapper1.all();
//        Assert.assertEquals(1, list4.size());
    }
}