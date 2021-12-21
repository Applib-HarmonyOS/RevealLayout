/*
 * Copyright (C) 2020-21 Application Library Engineering Group
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package per.goweii.reveallayout;

import ohos.aafwk.ability.delegation.AbilityDelegatorRegistry;
import ohos.app.Context;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class RevealLayoutOhosTest {

    private final Context context = AbilityDelegatorRegistry.getAbilityDelegator().getAppContext();
    private final RevealLayout revealLayout = new RevealLayout(context);

    @Test
    public void testBundleName() {
        final String actualBundleName = AbilityDelegatorRegistry.getArguments().getTestBundleName();
        assertEquals("per.goweii.reveallayout.hmservice", actualBundleName);
    }

    /**
     * setCheckedLayoutId test-case.
     */
    @Test
    public void testCheckedView() {
        revealLayout.setChecked(true);
        assertEquals(true, revealLayout.isChecked());
    }

    /**
     * setCheckedLayoutId test-case.
     */
    @Test
    public void testCheckedLayoutId() {
        revealLayout.setCheckedLayoutId(ResourceTable.Layout_reveal_layout_follow_checked);
        assertEquals(ResourceTable.Layout_reveal_layout_follow_checked, revealLayout.getCheckedLayoutId());
    }

    /**
    * setUncheckedLayoutId test-case.
    */
    @Test
    public void testUncheckedLayoutId() {
        revealLayout.setUncheckedLayoutId(ResourceTable.Layout_reveal_layout_follow_unchecked);
        assertEquals(ResourceTable.Layout_reveal_layout_follow_unchecked, revealLayout.getUncheckedLayoutId());
    }

    /**
     * CenterX value test-case.
     */
    @Test
    public void testCenterX() {
        revealLayout.setCenter(2.5f, 2.5f);
        assertEquals(2.5f, revealLayout.getCenterX(), 0.02f);
    }

    /**
     * CenterY value test-case.
     */
    @Test
    public void testCenterY() {
        revealLayout.setCenter(2.5f, 2.5f);
        assertEquals(2.5f, revealLayout.getCenterY(), 0.02f);
    }
}