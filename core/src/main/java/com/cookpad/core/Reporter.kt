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

import com.cookpad.core.models.Report

/**
 * A contract that any class eligible for behaving as an OkReport's reporter must to fulfil.
 * This provides the outer output point on the OkReport pipeline, meaning this is the class in charge of
 * sending the report to whatever external service is required.
 */
interface Reporter {
    fun sendReport(report: Report, reporterCallback: ReporterCallback)
}

/**
 * A simple callback lo let OkReport know about the result of the operation of sending a given report.
 */
interface ReporterCallback {
    fun success(message: String)
    fun error(error: Throwable)
}