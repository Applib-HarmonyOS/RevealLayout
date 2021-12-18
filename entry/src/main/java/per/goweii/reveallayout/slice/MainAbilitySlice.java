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

package per.goweii.reveallayout.slice;

import ohos.aafwk.ability.AbilitySlice;
import ohos.aafwk.content.Intent;
import per.goweii.reveallayout.ResourceTable;

/**
 * Ability to demo the RevealLayout view.
 */
public class MainAbilitySlice extends AbilitySlice {

    /**
     * Called when ability is starting.
     *
     * @param intent Intent
     */
    @Override
    public void onStart(Intent intent) {
        super.onStart(intent);
        super.setUIContent(ResourceTable.Layout_ability_main);
    }

    /**
     * Called when ability is in active.
     */
    @Override
    public void onActive() {
        super.onActive();
    }

    /**
     * Called when ability is in forground.
     */
    @Override
    public void onForeground(Intent intent) {
        super.onForeground(intent);
    }
}
