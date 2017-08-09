package com.cookpad.core.ui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import com.cookpad.core.R
import com.cookpad.core.data.OkReportRepository
import com.simplify.ink.InkView
import github.nisrulz.screenshott.ScreenShott


class CanvasActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.canvas_activity)

        setUpActionBar()
        setUpCanvasView()
        setUpScreenshotAsBackground()
        adjustViewsToActualImageSize()
    }

    private fun setUpActionBar() =
            supportActionBar?.apply {
                setTitle(R.string.highlight_screenshot)
                setDisplayHomeAsUpEnabled(true)
            }

    private fun setUpCanvasView() {
        val inkViewCanvas = findViewById<InkView>(R.id.inkViewCanvas)
        inkViewCanvas.setColor(ContextCompat.getColor(this, android.R.color.holo_red_light));

        inkViewCanvas.setMinStrokeWidth(5f)
        inkViewCanvas.setMaxStrokeWidth(7f)
    }

    private fun setUpScreenshotAsBackground() {
        val path = intent.extras.get(KEY_FILE) as String
        val iVScreenshot = findViewById<ImageView>(R.id.iVScreenshot)
        iVScreenshot.setImageBitmap(OkReportRepository.loadBitmap(path, applyInSampleSize = false))
    }

    private fun adjustViewsToActualImageSize() {
        val iVScreenshot = findViewById<ImageView>(R.id.iVScreenshot)

        iVScreenshot.post {
            val imageViewHeight = iVScreenshot.height
            val imageViewWidth = iVScreenshot.width

            val intrinsicHeight = iVScreenshot.drawable.intrinsicHeight
            val intrinsicWidth = iVScreenshot.drawable.intrinsicWidth

            val (actualWidth, actualHeight) = if (imageViewHeight * intrinsicWidth <= imageViewWidth * intrinsicHeight) {
                intrinsicWidth * imageViewHeight / intrinsicHeight to imageViewHeight
            } else {
                imageViewWidth to intrinsicHeight * imageViewWidth / intrinsicWidth
            }

            findViewById<View>(R.id.inkViewCanvas).apply {
                layoutParams.width = actualWidth
                layoutParams.height = actualHeight
                requestLayout()
            }

            findViewById<View>(R.id.rlRoot).apply {
                layoutParams.width = actualWidth
                layoutParams.height = actualHeight
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.canvas_activity_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean =
            when (item?.itemId) {
                android.R.id.home -> {
                    onBackPressed()
                    true
                }
                R.id.menu_item_clear -> {
                    findViewById<InkView>(R.id.inkViewCanvas).clear()
                    true
                }
                R.id.menu_item_ok -> {
                    saveAndFinishActivity()
                    true
                }
                else -> super.onOptionsItemSelected(item)
            }

    private fun saveAndFinishActivity() {
        val screenShot = ScreenShott.getInstance().takeScreenShotOfView(findViewById(R.id.rlRoot))
        val position = intent.extras.get(KEY_POSITION) as Int
        val name = "Step$position"
        OkReportRepository.saveBitmap(this, screenShot, name)

        val intent = Intent()
        intent.putExtra(KEY_POSITION, position)
        setResult(Activity.RESULT_OK, intent)

        finish()
    }
}
