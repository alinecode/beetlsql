package org.beetl.sql.springboot.simple;


import org.beetl.sql.core.SQLManager;
import org.beetl.sql.springboot.UserInfo;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = SimpleApplication.class)
@Transactional
public class SimpleTest {
    @Autowired
    SQLManager sqlManager;

    @Autowired
    SimpleUserInfoMapper userInfoMapper;
    @Test
    public void test(){
        sqlManager.single(UserInfo.class,1);
        userInfoMapper.single(1);
		userInfoMapper.select();
    }
}