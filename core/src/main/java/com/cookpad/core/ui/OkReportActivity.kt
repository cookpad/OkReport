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

package com.cookpad.core.ui

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.TypedValue
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import com.afollestad.materialdialogs.MaterialDialog
import com.cookpad.core.R
import com.cookpad.core.ReporterCallback
import com.cookpad.core.data.OkReportRepository
import com.cookpad.core.models.Report
import com.cookpad.core.models.Step
import miguelbcr.ok_adapters.recycler_view.OkRecyclerViewAdapter

internal val KEY_FILE = "key_file"
internal val KEY_POSITION = "key_position"
internal val REQUEST_CODE = 105

class OkReportActivity : AppCompatActivity() {
    private val updateStep: (Step, Int) -> Unit = { step, position ->
        if (position < okAdapter.items().size) {
            okAdapter.items()[position] = step
        }
    }

    private val removeStep: (Int) -> Unit = { position ->
        OkReportRepository.report.steps.removeAt(position)

        okAdapter.items().removeAt(position)
        okAdapter.notifyDataSetChanged()

        if (OkReportRepository.report.steps.isEmpty()) {
            finish()
        }
    }

    private val highlightScreenshot: (Step, Int) -> Unit = { (_, pathImage), position ->
        val intent = Intent(this, CanvasActivity::class.java)
        intent.putExtra(KEY_FILE, pathImage)
        intent.putExtra(KEY_POSITION, position)
        startActivityForResult(intent, REQUEST_CODE)
    }

    private var okAdapter = StepsAdapter(updateStep, removeStep, highlightScreenshot)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.ok_report_activity)

        setUpActionBar()
        setUpTitle()
        setUpRecyclerView()
    }

    private fun setUpActionBar() =
            supportActionBar?.apply {
                setTitle(R.string.new_report)

                val steps = OkReportRepository.report.steps.size
                subtitle = resources.getQuantityString(R.plurals.total_steps, steps, steps)

                setDisplayHomeAsUpEnabled(true)
            }

    private fun setUpTitle() = findViewById<EditText>(R.id.etTitle).setText(OkReportRepository.report.issue)


    private fun setUpRecyclerView() =
            findViewById<RecyclerView>(R.id.rvSteps).apply {
                layoutManager = LinearLayoutManager(this@OkReportActivity, LinearLayoutManager.HORIZONTAL, false)
                addItemDecoration(SpacesItemDecoration(this@OkReportActivity))

                okAdapter.addAll(OkReportRepository.report.steps)

                adapter = okAdapter

                post { scrollToPosition(adapter.itemCount - 1) }
            }

    private fun sendReport() {
        if (findViewById<EditText>(R.id.etTitle).text.toString().isEmpty()) {
            Toast.makeText(this, R.string.title_validation, Toast.LENGTH_LONG).show()
            return
        }

        syncReportChanges()

        val dialog = MaterialDialog.Builder(this)
                .title(R.string.sending_report)
                .content(R.string.please_wait)
                .progress(true, 0)
                .show()

        OkReportRepository.reporter.sendReport(OkReportRepository.report, object : ReporterCallback {
            override fun success(message: String) {
                dialog.dismiss()
                Toast.makeText(this@OkReportActivity, message, Toast.LENGTH_LONG).show()

                //syncReportChanges is called at onDestroy so we have to clear those dependencies from which the report takes its values
                findViewById<EditText>(R.id.etTitle).setText("")
                okAdapter.items().clear()

                finish()
            }

            override fun error(error: Throwable) {
                dialog.dismiss()
                Toast.makeText(this@OkReportActivity, error.message, Toast.LENGTH_LONG).show()
            }
        })
    }

    private fun syncReportChanges() {
        val etTitle = findViewById<EditText>(R.id.etTitle)
        OkReportRepository.report = Report(etTitle.text.toString(), okAdapter.items())
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.ok_report_activity_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean =
            when (item?.itemId) {
                android.R.id.home -> {
                    onBackPressed()
                    true
                }
                R.id.menu_item_send_report -> {
                    sendReport()
                    true
                }
                else -> super.onOptionsItemSelected(item)
            }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            data?.extras?.let {
                val position = it.get(KEY_POSITION) as Int
                okAdapter.notifyItemChanged(position)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        syncReportChanges()
    }
}

private class SpacesItemDecoration(context: Context) : RecyclerView.ItemDecoration() {
    var space = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10F, context.resources.displayMetrics).toInt()

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State?) {
        outRect.right = space
        outRect.bottom = space

        if (parent.getChildAdapterPosition(view) == 0) {
            outRect.left = space
        }
    }
}

private class StepsAdapter(val updateStep: (Step, Int) -> Unit, val removeStep: (Int) -> Unit, val highlightScreenshot: (Step, Int) -> Unit) : OkRecyclerViewAdapter<Step, StepViewGroup>() {
    override fun onCreateItemView(parent: ViewGroup, viewType: Int): StepViewGroup {
        return StepViewGroup(parent.context, updateStep, removeStep, highlightScreenshot)
    }

    fun items(): MutableList<Step> = items
}