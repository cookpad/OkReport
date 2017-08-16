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
 */package com.cookpad.okreport

import android.app.Application
import android.content.Context
import android.hardware.SensorManager
import com.cookpad.core.OkReport
import com.cookpad.core.collectDeviceSpecs
import com.cookpad.core.initOkReport
import com.cookpad.slack_reporter.SlackReporter
import com.squareup.seismic.ShakeDetector

class OkReportApp : Application() {
    companion object {
        lateinit var okReport: OkReport
    }

    override fun onCreate() {
        super.onCreate()

        val slackReporter = SlackReporter(token, webhookURL, collectDeviceSpecs(this), nameChannelImages, notifyChannel = false)
        okReport = initOkReport(this, slackReporter)

        val sensorManager = this.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        ShakeDetector({ okReport.trigger() }).start(sensorManager)
    }
}
