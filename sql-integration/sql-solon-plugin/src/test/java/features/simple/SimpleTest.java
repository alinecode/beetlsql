package features.simple;

import org.junit.jupiter.api.Test;
import org.noear.solon.annotation.Import;
import org.noear.solon.annotation.Inject;
import org.noear.solon.test.SolonTest;

@Import(profiles = "application-simple.properties")
@SolonTest
public class SimpleTest {
    @Inject
    SimpleService service;

    @Test
    public void test(){
        service.test();
    }
}
