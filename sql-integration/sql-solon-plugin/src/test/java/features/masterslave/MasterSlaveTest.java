package features.masterslave;

import org.junit.jupiter.api.Test;
import org.noear.solon.annotation.Inject;
import org.noear.solon.test.SolonTest;

@SolonTest(MasterSlaveApp.class)
public class MasterSlaveTest {
    @Inject
    MasterSlaveService service;

    @Test
    public void test(){
        service.test();
    }
}
