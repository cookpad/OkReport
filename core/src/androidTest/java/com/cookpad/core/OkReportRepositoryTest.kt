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

import android.graphics.BitmapFactory
import android.support.test.InstrumentationRegistry
import android.support.test.runner.AndroidJUnit4
import com.cookpad.core.data.OkReportRepository
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.not
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class OkReportRepositoryTest {

    @Test fun verifySaveAndLoadBitmap() {
        val context = InstrumentationRegistry.getTargetContext()
        val originalBitmap = BitmapFactory.decodeResource(context.resources, R.drawable.abc_ic_star_black_48dp)

        val path = OkReportRepository.saveBitmap(context, originalBitmap, "testImage")
        assertThat(path, `is`("/data/user/0/com.cookpad.core.test/app_imageDir/testImage.jpg"))

        val savedBitmap = OkReportRepository.loadBitmap(path)
        assertThat(savedBitmap.byteCount, `is`(not(0)))
    }

    @Test fun verifySaveAndRetrieveAuthor() {
        val someAuthor = "someAuthor"
        val context = InstrumentationRegistry.getTargetContext()

        OkReportRepository.saveAuthor(context, someAuthor)

        assertThat(OkReportRepository.retrieveAuthor(context), `is`(someAuthor))
    }

}
