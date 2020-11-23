package org.beetl.sql.ext.solon.test.simple;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.noear.solon.annotation.XInject;
import org.noear.solon.test.SolonJUnit4ClassRunner;
import org.noear.solon.test.SolonTest;

@RunWith(SolonJUnit4ClassRunner.class)
@SolonTest(SimpleApp.class)
public class SimpleTest {
    @XInject
    SimpleService service;

    @Test
    public void test(){
        service.test();
    }
}