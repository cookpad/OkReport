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

import org.json.JSONObject
import java.io.*
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLConnection

internal fun uploadFilesToSlack(token: String, pathUrls: List<String>, idOrNameChannelImages: String): List<String> {
    return pathUrls.map {
        val uploadUrl = "https://slack.com/api/files.upload"

        val multipart = MultipartUtility(uploadUrl, "UTF-8")

        multipart.addFormField("token", token)
        multipart.addFormField("channels", idOrNameChannelImages)

        multipart.addFilePart("file", File(it))

        val jsonFile = JSONObject(multipart.finish()).getJSONObject("file")
        jsonFile.getString("url_private")
    }
}

internal fun postToSlack(url: String, payload: String): String {
    var urlConnection: HttpURLConnection? = null

    try {
        urlConnection = (URL(url).openConnection() as HttpURLConnection).apply {
            readTimeout = 10000
            connectTimeout = 15000
            requestMethod = "POST"
            doInput = true
            doOutput = true
        }

        BufferedWriter(OutputStreamWriter(urlConnection.outputStream, "UTF-8")).use {
            it.write(payload)
            it.flush()
        }

        urlConnection.connect()

        val inputStream = BufferedInputStream(urlConnection.inputStream)
        val response = inputStream.bufferedReader().use { it.readText() }

        return response.apply {
            if (contains("error")) throw IOException(this)
        }
    } finally {
        urlConnection?.disconnect()
    }
}

private class MultipartUtility(requestUrl: String, private val charset: String) {
    private val boundary = "===" + System.currentTimeMillis() + "==="
    private val urlConnection = (URL(requestUrl).openConnection() as HttpURLConnection).apply {
        readTimeout = 10000
        connectTimeout = 15000
        requestMethod = "POST"
        doInput = true
        doOutput = true
        setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary)
    }

    private val outputStream by lazy { urlConnection.outputStream }
    private val writer by lazy { PrintWriter(OutputStreamWriter(outputStream, charset), true) }
    private val LINE_FEED = "\r\n"

    internal fun addFormField(name: String, value: String) {
        writer.append("--" + boundary).append(LINE_FEED)
        writer.append("Content-Disposition: form-data; name=\"" + name + "\"").append(LINE_FEED)
        writer.append("Content-Type: text/plain; charset=" + charset).append(LINE_FEED)
        writer.append(LINE_FEED)
        writer.append(value).append(LINE_FEED)
        writer.flush()
    }

    internal fun addFilePart(fieldName: String, uploadFile: File) {
        val fileName = uploadFile.name

        writer.append("--" + boundary).append(LINE_FEED)
        writer.append("Content-Disposition: form-data; name=\"$fieldName\"; filename=\"$fileName\"").append(LINE_FEED)
        writer.append("Content-Type: " + URLConnection.guessContentTypeFromName(fileName)).append(LINE_FEED)
        writer.append("Content-Transfer-Encoding: binary").append(LINE_FEED)
        writer.append(LINE_FEED)
        writer.flush()

        FileInputStream(uploadFile).use { it.copyTo(outputStream) }
        outputStream.flush()

        writer.append(LINE_FEED)
        writer.flush()
    }

    fun finish(): String {
        val response = StringBuffer()

        writer.append(LINE_FEED).flush()
        writer.append("--$boundary--").append(LINE_FEED)
        writer.close()

        val input = BufferedInputStream(urlConnection.inputStream)
        response.append(input.bufferedReader().use { it.readText() })
        urlConnection.disconnect()

        return response.toString().apply {
            if (contains("error")) throw IOException(this)
        }
    }
}