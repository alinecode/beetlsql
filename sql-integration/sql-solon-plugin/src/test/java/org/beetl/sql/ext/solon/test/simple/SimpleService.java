package org.beetl.sql.ext.solon.test.simple;

import org.beetl.sql.core.SQLManager;
import org.beetl.sql.ext.solon.Db;
import org.beetl.sql.ext.solon.test.UserInfo;
import org.noear.solon.extend.aspect.annotation.Service;
import org.noear.solon.extend.data.annotation.Tran;

/**
 * Solon 的事务，只支持 XController, XService, XDao ，且只支持注在函数上（算是较为克制）
 * */
@Service
public class SimpleService {
    @Db
    SQLManager sqlManager;

    @Db
    SimpleUserInfoMapper userInfoMapper;

    @Tran
    public void test(){
        sqlManager.single(UserInfo.class,1);
        userInfoMapper.single(1);
    }
}
