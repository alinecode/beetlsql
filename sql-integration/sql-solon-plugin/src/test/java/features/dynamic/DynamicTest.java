package features.dynamic;

import org.junit.jupiter.api.Test;
import org.noear.solon.annotation.Import;
import org.noear.solon.annotation.Inject;
import org.noear.solon.test.SolonTest;

@Import(profiles = "application-dynamic.properties")
@SolonTest
public class DynamicTest {
    @Inject
    DynamicService single;

    @Test
    public void test(){
        single.test();
    }
}
