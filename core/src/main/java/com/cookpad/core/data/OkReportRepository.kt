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

package com.cookpad.core.data

import android.content.Context
import android.content.ContextWrapper
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.cookpad.core.Reporter
import com.cookpad.core.models.Report
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream

object OkReportRepository {
    internal lateinit var reporter: Reporter
    internal var report: Report = Report("", mutableListOf())

    /**
     * Helper method to save a bitmap.
     */
    fun saveBitmap(context: Context, bitmapImage: Bitmap, imageName: String): String {
        val directory = ContextWrapper(context).getDir("imageDir", Context.MODE_PRIVATE)
        val file = File(directory, "$imageName.jpg")

        var fileOutputStream: FileOutputStream? = null
        try {
            fileOutputStream = FileOutputStream(file)
            bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream)
        } finally {
            fileOutputStream?.close()
        }

        return file.absolutePath
    }

    /**
     * Helper method to retrieve a previously saved bitmap using OkReportRepository::saveBitmap
     * based on the path supplied.
     */
    fun loadBitmap(path: String, applyInSampleSize: Boolean = true): Bitmap {
        val options = BitmapFactory.Options()
        if (applyInSampleSize) {
            options.inSampleSize = 2
        }
        return BitmapFactory.decodeStream(FileInputStream(File(path)), null, options)
    }
}