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

import android.content.Context
import android.content.res.Resources
import android.os.Build
import android.util.DisplayMetrics

data class DeviceSpecs(val deviceMake: String, val deviceModel: String, val deviceResolution: String,
                       val deviceDensity: String, val versionCode: String, val versionRelease: String,
                       val androidVersion: String, val locale: String)

/**
 * Handy information about the device and the build version, such us device model, locale or current version code.
 */
fun collectDeviceSpecs(context: Context): DeviceSpecs {
    val displayMetrics = context.resources.displayMetrics
    val densityBucket = getDensityString(displayMetrics)
    val deviceMake = Build.MANUFACTURER
    val deviceModel = Build.MODEL
    val deviceResolution = "${displayMetrics.heightPixels}x${displayMetrics.widthPixels}"
    val deviceDensity = "${displayMetrics.densityDpi}dpi ($densityBucket)"
    val versionCode = versionCode(context)
    val versionRelease = Build.VERSION.RELEASE;
    val androidVersion = Build.VERSION.SDK_INT.toString()
    val locale = defaultLocale()

    return DeviceSpecs(deviceMake, deviceModel, deviceResolution, deviceDensity, versionCode, versionRelease, androidVersion, locale)
}

private fun getDensityString(displayMetrics: DisplayMetrics): String =
        when (displayMetrics.densityDpi) {
            DisplayMetrics.DENSITY_LOW -> "ldpi"
            DisplayMetrics.DENSITY_MEDIUM -> "mdpi"
            DisplayMetrics.DENSITY_HIGH -> "hdpi"
            DisplayMetrics.DENSITY_XHIGH -> "xhdpi"
            DisplayMetrics.DENSITY_XXHIGH -> "xxhdpi"
            DisplayMetrics.DENSITY_XXXHIGH -> "xxxhdpi"
            DisplayMetrics.DENSITY_TV -> "tvdpi"
            else -> displayMetrics.densityDpi.toString()
        }

private fun versionCode(context: Context): String =
        context.packageManager.getPackageInfo(context.packageName, 0).versionCode.toString()

private fun defaultLocale() =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Resources.getSystem().configuration.locales[0].language
        } else {
            Resources.getSystem().configuration.locale.language
        }



