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

/**
 * A contract that any class eligible for behaving as an OkReport's trigger must to fulfil.
 * This trigger launches OKReport main screen. It may be a click listener, a shake gesture, or whatever gesture that suits better.
 */
interface TriggerGesture {
    fun onTrigger(callback: () -> Unit)
}

