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

package com.cookpad.slack_reporter


import android.os.AsyncTask
import android.support.annotation.VisibleForTesting
import com.cookpad.core.DeviceSpecs
import com.cookpad.core.Reporter
import com.cookpad.core.ReporterCallback
import com.cookpad.core.models.Report
import com.cookpad.core.models.Step
import org.json.JSONArray
import org.json.JSONObject

/**
 * SlackReporter fulfills the Reporter interface by posting the data retrieved from OkReport into an Slack channel
 * making use of incoming webhook Slack technology. For more info: https://api.slack.com/incoming-webhooks.
 *
 * @property token a valid Slack legacy token required to upload the images to Slack hosts. Go to https://api.slack.com/custom-integrations/legacy-tokens and create one.
 * @property webhookURL the url of the webhook in which SlackReporter will rely to perform the publishing report. Go to https://api.slack.com/incoming-webhooks and create one.
 * @property idOrNameChannelImages the name or id of the channel where report's image should be published. This is due to some current Slack's Api limitations does not allow
 * to show an image on a posted message without the image been previously shared individually on a channel.
 * @property notifyChannel false by default, if true, it triggers desktop and push notifications to all team members in the channel where the report has been posted.
 */
class SlackReporter(val token: String, val webhookURL: String, val deviceSpecs: DeviceSpecs,
                    val idOrNameChannelImages: String, val notifyChannel: Boolean = false) : Reporter {
    override fun sendReport(report: Report, reporterCallback: ReporterCallback) {
        val asyncTask = object : AsyncTask<String, Void, Response>() {

            override fun doInBackground(vararg ignored: String): Response {
                try {
                    val aColor = pickRandomColor()
                    val urlImages = uploadFilesToSlack(token, report.steps.map(Step::pathImage), idOrNameChannelImages)

                    buildJsonTitle(aColor, report.issue, report.author, notifyChannel).let { postToSlack(webhookURL, it) }
                    buildJsonSteps(aColor, urlImages, report.steps).forEach { postToSlack(webhookURL, it) }
                    buildJsonDeviceSpecs(aColor, deviceSpecs).let { postToSlack(webhookURL, it) }

                    return Success("Report successfully sent to slack")
                } catch (error: Exception) {
                    return Failure(error)
                }
            }

            override fun onPostExecute(response: Response) {
                when (response) {
                    is Success -> reporterCallback.success(response.message)
                    is Failure -> reporterCallback.error(response.error)
                }
            }
        }

        asyncTask.execute("")
    }
}

internal val colors = listOf("#8DB6CD", "#fff3bc", "#f2981c", "#efcabd", "#00c78c", "#e9d2c9")
internal fun pickRandomColor(): String {
    val index = (Math.random() * ((colors.size - 1) + 1)).toInt()
    return colors[index]
}

@VisibleForTesting
internal fun buildJsonTitle(hexColor: String, reportTitle: String, author: String, notifyChannel: Boolean): String {
    val notification = if (notifyChannel) "<!channel> " else ""
    val title = "$notification *New Report* from @$author"

    val jsonAttachment = JSONObject().put("fallback", title)
            .put("pretext", title)
            .put("mrkdwn_in", JSONArray().put("pretext"))
            .put("color", hexColor)
            .put("fields", JSONArray().put(JSONObject()
                    .put("title", "What's the issue?")
                    .put("value", reportTitle)))

    return JSONObject().put("attachments", JSONArray().put(jsonAttachment)).toString()
}

@VisibleForTesting
internal fun buildJsonSteps(hexColor: String, urlImages: List<String>, steps: List<Step>): List<String> {
    val payloads = mutableListOf<String>()

    steps.forEachIndexed { index, step ->
        val jsonAttachment = JSONObject()
        val title = "Step ${index + 1}"

        jsonAttachment.put("fallback", title)
        jsonAttachment.put("title", title)
        jsonAttachment.put("text", step.title)
        jsonAttachment.put("image_url", urlImages[index])
        jsonAttachment.put("color", hexColor)

        val payload = JSONObject().put("attachments", JSONArray().put(jsonAttachment)).toString()
        payloads.add(payload)
    }

    return payloads.toList()
}

@VisibleForTesting
internal fun buildJsonDeviceSpecs(hexColor: String, deviceSpecs: DeviceSpecs): String {

    val jsonAttachment = JSONObject()
            .put("color", hexColor)
            .put("fields", JSONArray()
                    .put(JSONObject().put("title", "Device Make").put("value", deviceSpecs.deviceMake).put("short", true))
                    .put(JSONObject().put("title", "Device Model").put("value", deviceSpecs.deviceModel).put("short", true))
                    .put(JSONObject().put("title", "Device Resolution").put("value", deviceSpecs.deviceResolution).put("short", true))
                    .put(JSONObject().put("title", "Device Density").put("value", deviceSpecs.deviceDensity).put("short", true))
                    .put(JSONObject().put("title", "Version Code").put("value", deviceSpecs.versionCode).put("short", true))
                    .put(JSONObject().put("title", "Version Release").put("value", deviceSpecs.versionRelease).put("short", true))
                    .put(JSONObject().put("title", "Android Version").put("value", deviceSpecs.androidVersion).put("short", true))
                    .put(JSONObject().put("title", "Locale").put("value", deviceSpecs.locale).put("short", true))
            )

    return JSONObject().put("attachments", JSONArray().put(jsonAttachment)).toString()
}

private sealed class Response
private data class Success(val message: String) : Response()
private data class Failure(val error: Throwable) : Response()