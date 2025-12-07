package org.beetl.sql.springboot.datasource2;


import org.beetl.sql.springboot.datasource2.db1.Db1Service;
import org.beetl.sql.springboot.datasource2.db2.Db2Service;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = TwoDBApplication.class)
public class SimpleTest {
	@Autowired
	Db1Service service1;
	@Autowired
	Db2Service service2;

    @Test
    public void test(){
    	service1.queryUser(1);
		service2.queryUser(2);
    }


}
