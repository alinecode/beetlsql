package org.beetl.sql.ext.solon.test.dynamic;

import org.beetl.sql.core.SQLManager;
import org.beetl.sql.ext.solon.Db;
import org.noear.solon.annotation.XTran;
import org.noear.solon.extend.aspect.annotation.XService;

/**
 * Solon 的事务，只支持 XController, XService, XDao ，且只支持注在函数上（算是较为克制）
 * */
@XService
public class DynamicService {
    @Db
    SQLManager sqlManager;

    @Db
    DynamicUserInfoMapper mapper;

    @XTran
    public void test(){
        mapper.deleteById(19999);
        sqlManager.single(UserInfoInDs1.class,1);
        sqlManager.single(UserInfoInDs2.class,1);
        mapper.single(1);
        mapper.queryById(1);
    }
}
