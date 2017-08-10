package com.cookpad.slack_reporter

import android.graphics.BitmapFactory
import android.os.Looper
import android.support.test.InstrumentationRegistry
import android.support.test.runner.AndroidJUnit4
import com.cookpad.core.ReporterCallback
import com.cookpad.core.collectDeviceSpecs
import com.cookpad.core.data.OkReportRepository.saveBitmap
import com.cookpad.core.models.Report
import com.cookpad.core.models.Step
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.nullValue
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
class SlackReporterIntegrationTest {
    private val report = Report("Ignore this message. This is an automated OKReport test", "authorTest", mutableListOf())

    @Test fun verifySlackReporterSuccess() {
        var success: String? = null
        var fails: Throwable? = null

        verifySlackReporter(true, { success = it }, { fails = it })

        assertThat(success, `is`("Report successfully sent to slack"))
        assertThat(fails, `is`(nullValue()))
    }

    @Test fun verifySlackReporterFails() {
        var success: String? = null
        var fails: Throwable? = null

        verifySlackReporter(false, { success = it }, { fails = it })

        assertThat(success, `is`(nullValue()))
        assertThat(fails!!.message, `is`("{\"ok\":false,\"error\":\"invalid_auth\"}"))
    }

    private fun verifySlackReporter(success: Boolean, successCallback: (String) -> Unit, failureCallback: (Throwable) -> Unit) {
        val context = InstrumentationRegistry.getTargetContext()
        val icon = BitmapFactory.decodeResource(context.resources, R.drawable.abc_ic_star_black_48dp)
        val path = saveBitmap(context, icon, "testImage")

        report.steps.add(Step("Ignore it", path))

        val aToken = if (success) token else "wrong"
        val slackReporter = SlackReporter(aToken, webhookURL, collectDeviceSpecs(context), nameChannelImages)

        slackReporter.sendReport(report, object : ReporterCallback {
            override fun success(message: String) {
                val isMainThread = Looper.myLooper() == Looper.getMainLooper()
                assertThat(isMainThread, `is`(true))
                successCallback(message)
            }

            override fun error(error: Throwable) {
                val isMainThread = Looper.myLooper() == Looper.getMainLooper()
                assertThat(isMainThread, `is`(true))
                failureCallback(error)
            }
        })

        Thread.sleep(4000)
    }
}
