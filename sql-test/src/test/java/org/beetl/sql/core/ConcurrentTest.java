package org.beetl.sql.core;

import org.beetl.sql.BaseTest;
import org.beetl.sql.core.page.DefaultPageRequest;
import org.beetl.sql.core.page.PageRequest;
import org.beetl.sql.core.page.PageResult;
import org.beetl.sql.entity.User;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;

/**
 * 测试内置的查询操作
 */
public class ConcurrentTest extends BaseTest {
    @BeforeClass
    public static void init(){
        initTable(testSqlFile);
    }

    @Test
    public void testConcurrentSelect() throws Exception{
		int eprId=0;
		ExecutorService exec = Executors.newFixedThreadPool(11);
		Future[] futures = new Future[11];
		for (int i = 0; i < 11; i++) {
			futures[i] = exec.submit(new Runnable() {
				@Override
				public void run() {
					User user = select();
				}
			});

		}
		for (int i = 0; i < 11; i++) {
			futures[i].get();
		}
    }

    protected  User select(){
    	User user = sqlManager.lambdaQuery(User.class).andEq(User::getId,1).single();
    	return user;
	}



}
