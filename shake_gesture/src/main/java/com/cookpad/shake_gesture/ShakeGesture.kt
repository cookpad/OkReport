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

package com.cookpad.shake_gesture

import android.content.Context
import android.content.Context.SENSOR_SERVICE
import android.hardware.SensorManager
import com.cookpad.core.TriggerGesture
import com.squareup.seismic.ShakeDetector

/**
 * ShakeGesture fulfills the TriggerGesture interface by triggering the report screen when the device is shaken.
 */
class ShakeGesture(context: Context) : TriggerGesture, ShakeDetector.Listener {
    var callback: (() -> Unit)? = null

    init {
        val sensorManager = context.getSystemService(SENSOR_SERVICE) as SensorManager
        ShakeDetector(this).start(sensorManager)
    }

    override fun onTrigger(callback: () -> Unit) {
        this.callback = callback
    }

    override fun hearShake() {
        callback?.invoke()
    }
}
