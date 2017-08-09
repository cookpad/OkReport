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

import android.app.Activity
import android.app.Application
import android.content.Intent
import android.os.Bundle
import android.view.View
import com.cookpad.core.data.OkReportRepository
import com.cookpad.core.models.Step
import com.cookpad.core.ui.OkReportActivity
import github.nisrulz.screenshott.ScreenShott

/**
 * Entry point to start OkReport. Call it just one time per life-time application.
 *
 * @property application an Android application instance.
 * @property triggerGesture a valid implementation of TriggerGesture interface.
 * @property reporter a valid implementation of Reporter interface.
 */
fun initOkReport(application: Application,
                 triggerGesture: TriggerGesture,
                 reporter: Reporter) {
    OkReportRepository.reporter = reporter

    var liveActivity: Activity? = null
    var okReportActivityIsAtFront = false

    application.registerActivityLifecycleCallbacks(object : Application.ActivityLifecycleCallbacks {
        override fun onActivityResumed(activity: Activity?) {
            liveActivity = activity
        }

        override fun onActivityPaused(activity: Activity?) {
            liveActivity = null
        }

        override fun onActivityStarted(activity: Activity?) {
            if (activity is OkReportActivity) {
                okReportActivityIsAtFront = true
            }
        }

        override fun onActivityDestroyed(activity: Activity?) {
            if (activity is OkReportActivity) {
                okReportActivityIsAtFront = false
            }
        }

        override fun onActivityStopped(activity: Activity?) {}
        override fun onActivitySaveInstanceState(activity: Activity?, bundle: Bundle?) {}
        override fun onActivityCreated(activity: Activity?, bundle: Bundle?) {}
    })

    fun takeScreenShot(activity: Activity): String {
        val root = activity.findViewById<View>(android.R.id.content)
        val screenShot = ScreenShott.getInstance().takeScreenShotOfView(root)
        val name = "Step${OkReportRepository.report.steps.size}"
        return OkReportRepository.saveBitmap(activity, screenShot, name)
    }

    triggerGesture.onTrigger {
        if (okReportActivityIsAtFront) return@onTrigger
        okReportActivityIsAtFront = true

        liveActivity?.let {
            val pathImage = takeScreenShot(it)
            val step = Step("", pathImage)
            OkReportRepository.report.steps.add(step)

            val intent = Intent(it, OkReportActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            it.startActivity(intent)
        }
    }
}