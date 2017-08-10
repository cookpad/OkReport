package com.cookpad.slack_reporter

import com.cookpad.core.DeviceSpecs
import com.cookpad.core.models.Step
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Test

class SlackReporterTest {

    @Test fun verifyBuildJsonTitle() {
        val hexColor = "#8DB6CD"
        val reportTitle = "A title report"
        val jsonTitle = buildJsonTitle(hexColor, reportTitle, "authorTest", true)
        val expectedJsonTitle = "{\"attachments\":[{\"color\":\"#8DB6CD\",\"mrkdwn_in\":[\"pretext\"],\"pretext\":\"<!channel>  *New Report* from @authorTest\",\"fields\":[{\"title\":\"What's the issue?\",\"value\":\"A title report\"}],\"fallback\":\"<!channel>  *New Report* from @authorTest\"}]}"
        assertThat(jsonTitle, `is`(expectedJsonTitle))
    }

    @Test fun verifyBuildJsonSteps() {
        val hexColor = "#8DB6CD"
        val urlImages = listOf("u1", "u2", "u3")
        val steps = listOf(Step("s1", ""), Step("s2", ""), Step("s3", ""))

        val jsonSteps = buildJsonSteps(hexColor, urlImages, steps)

        val expectedJsonStep1 = "{\"attachments\":[{\"color\":\"#8DB6CD\",\"image_url\":\"u1\",\"text\":\"s1\",\"title\":\"Step 1\",\"fallback\":\"Step 1\"}]}"
        val expectedJsonStep2 = "{\"attachments\":[{\"color\":\"#8DB6CD\",\"image_url\":\"u2\",\"text\":\"s2\",\"title\":\"Step 2\",\"fallback\":\"Step 2\"}]}"
        val expectedJsonStep3 = "{\"attachments\":[{\"color\":\"#8DB6CD\",\"image_url\":\"u3\",\"text\":\"s3\",\"title\":\"Step 3\",\"fallback\":\"Step 3\"}]}"

        assertThat(jsonSteps.size, `is`(3))
        assertThat(jsonSteps[0], `is`(expectedJsonStep1))
        assertThat(jsonSteps[1], `is`(expectedJsonStep2))
        assertThat(jsonSteps[2], `is`(expectedJsonStep3))
    }

    @Test fun verifyBuildJsonDeviceSpecs() {
        val hexColor = "#8DB6CD"
        val deviceSpecs = DeviceSpecs("Google", "Android SDK built for x86", "1794x1080", "420dpi (420)", "0", "7.0", "24", "en")
        val jsonDeviceSpecs = buildJsonDeviceSpecs(hexColor, deviceSpecs)
        val expectedJsonDeviceSpecs = "{\"attachments\":[{\"color\":\"#8DB6CD\",\"fields\":[{\"short\":true,\"title\":\"Device Make\",\"value\":\"Google\"},{\"short\":true,\"title\":\"Device Model\",\"value\":\"Android SDK built for x86\"},{\"short\":true,\"title\":\"Device Resolution\",\"value\":\"1794x1080\"},{\"short\":true,\"title\":\"Device Density\",\"value\":\"420dpi (420)\"},{\"short\":true,\"title\":\"Version Code\",\"value\":\"0\"},{\"short\":true,\"title\":\"Version Release\",\"value\":\"7.0\"},{\"short\":true,\"title\":\"Android Version\",\"value\":\"24\"},{\"short\":true,\"title\":\"Locale\",\"value\":\"en\"}]}]}"
        assertThat(jsonDeviceSpecs, `is`(expectedJsonDeviceSpecs))
    }

    @Test fun verifyPickRandomColor() {
        for (i in 1..1000) {
            val color = pickRandomColor()
            assertThat(colors.joinToString().contains(color), `is`(true))
        }
    }

}
