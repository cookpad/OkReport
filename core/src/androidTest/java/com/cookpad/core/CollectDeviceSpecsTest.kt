/*
 * Copyright 2017 Cookpad Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.cookpad.core

import android.support.test.InstrumentationRegistry
import android.support.test.runner.AndroidJUnit4
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class CollectDeviceSpecsTest {

    /**
     * Run this test with Nexus 5x Api 24
     */
    @Test fun verifyCollectPhoneSpecs() {
        val context = InstrumentationRegistry.getTargetContext()
        val phoneSpecs = collectDeviceSpecs(context)

        assertThat(phoneSpecs.deviceMake, `is`("Google"))
        assertThat(phoneSpecs.deviceModel, `is`("Android SDK built for x86"))
        assertThat(phoneSpecs.deviceResolution, `is`("1794x1080"))
        assertThat(phoneSpecs.deviceDensity, `is`("420dpi (420)"))
        assertThat(phoneSpecs.versionCode, `is`("0"))
        assertThat(phoneSpecs.versionRelease, `is`("7.0"))
        assertThat(phoneSpecs.androidVersion, `is`("24"))
        assertThat(phoneSpecs.locale, `is`("en"))
    }
}
