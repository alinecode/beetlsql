package features.masterslave;

import org.junit.jupiter.api.Test;
import org.noear.solon.annotation.Import;
import org.noear.solon.annotation.Inject;
import org.noear.solon.test.SolonTest;

@Import(profiles = "application-master-slave.properties")
@SolonTest
public class MasterSlaveTest {
    @Inject
    MasterSlaveService service;

    @Test
    public void test(){
        service.test();
    }
}
