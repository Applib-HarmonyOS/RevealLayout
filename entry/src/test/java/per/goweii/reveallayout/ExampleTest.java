package per.goweii.reveallayout;

import ohos.aafwk.ability.delegation.AbilityDelegatorRegistry;
import org.junit.Test;
import static org.junit.Assert.assertEquals;

public class ExampleTest {
    @Test
    public void testBundleName() {
        final String actualBundleName = AbilityDelegatorRegistry.getArguments().getTestBundleName();
        assertEquals("per.goweii.reveallayout", actualBundleName);
    }
}
