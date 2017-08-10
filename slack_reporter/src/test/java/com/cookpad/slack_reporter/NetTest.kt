package com.cookpad.slack_reporter

import junit.framework.Assert.fail
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.containsString
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Test
import java.io.IOException

class NetTest {
    private val pathImage = "./assets/intro.png"

    @Test fun verifyUploadFilesToSlack() {
        val pathUrls = mutableListOf(pathImage, pathImage)
        val urls = uploadFilesToSlack(token, pathUrls, nameChannelImages)

        assertThat(urls.size, `is`(2))
        assertThat(urls[0], containsString("https://files.slack.com/files-pri/"))
        assertThat(urls[1], containsString("https://files.slack.com/files-pri/"))
    }

    @Test fun verifyUploadFilesToSlackWrongToken() {
        val pathUrls = mutableListOf(pathImage, pathImage)
        try {
            uploadFilesToSlack("3433434", pathUrls, nameChannelImages)
            fail("Must throw!")
        } catch (expectedException: IOException) {
            assertThat(expectedException.message, `is`("{\"ok\":false,\"error\":\"invalid_auth\"}"))
        }
    }

    @Test fun verifyPostMessageOnSlack() {
        val jsonTitle = buildJsonTitle("#8DB6CD", "Ignore this message. This is an automated OKReport test", "authorTest", false)
        val response = postToSlack(webhookURL, jsonTitle)
        assertThat(response, `is`("ok"))
    }

    @Test fun verifyPostMessageOnSlackWrongWebhookURL() {
        val jsonTitle = buildJsonTitle("#8DB6CD", "Ignore this message. This is an automated OKReport test", "authorTest", false)

        try {
            postToSlack("wrongWebhook", jsonTitle)
            fail("Must throw!")
        } catch (expectedException: IOException) {
            assertThat(expectedException.message, `is`("no protocol: wrongWebhook"))
        }
    }
}
