package org.beetl.sql.springboot.masterslave;


import org.beetl.sql.core.SQLManager;
import org.beetl.sql.springboot.UserInfo;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = MasterSlaveApplication.class)
@Transactional
public class MasterSlaveTest {
    @Autowired
    SQLManager sqlManager;
    @Autowired
    MasterSlaveUserInfoMapper userInfoMapper;

    @Test
    public void test(){
        userInfoMapper.deleteById(19999);
        sqlManager.single(UserInfo.class,1);
        userInfoMapper.single(1);
    }
}