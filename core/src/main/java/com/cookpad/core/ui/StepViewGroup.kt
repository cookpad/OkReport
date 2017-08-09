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

import android.content.Context
import android.graphics.Point
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import com.cookpad.core.R
import com.cookpad.core.data.OkReportRepository
import com.cookpad.core.models.Step
import miguelbcr.ok_adapters.recycler_view.OkRecyclerViewAdapter


class StepViewGroup(context: Context, val updateStep: (Step, Int) -> Unit, val removeStep: (Int) -> Unit, val highlightScreenshot: (Step, Int) -> Unit) : FrameLayout(context), OkRecyclerViewAdapter.Binder<Step> {

    init {
        LayoutInflater.from(getContext()).inflate(R.layout.step_view_group, this, true)
    }

    override fun bind(step: Step, position: Int, count: Int) {
        setUpImageView(step)

        findViewById<TextView>(R.id.tvNumberStep).text = context.getString(R.string.step_number, position + 1)

        setUpEditText(step, position)

        findViewById<View>(R.id.btRemoveStep).setOnClickListener { removeStep(position) }
        findViewById<View>(R.id.btHighlight).setOnClickListener { highlightScreenshot(step, position) }
    }

    private fun setUpImageView(step: Step) =
            findViewById<ImageView>(R.id.ivStep).apply {
                setImageBitmap(OkReportRepository.loadBitmap(step.pathImage))

                val display = (context.getSystemService(Context.WINDOW_SERVICE) as WindowManager).defaultDisplay
                val size = Point()
                display.getSize(size)

                layoutParams.width = (size.x * 0.60).toInt()
            }

    private fun setUpEditText(step: Step, position: Int) =
            findViewById<EditText>(R.id.etStep).apply {
                setText(step.title)

                addTextChangedListener(object : TextWatcher {
                    override fun afterTextChanged(editable: Editable) {
                        updateStep(Step(editable.toString(), step.pathImage), position)
                    }

                    override fun beforeTextChanged(text: CharSequence, start: Int, count: Int, after: Int) {}

                    override fun onTextChanged(text: CharSequence, start: Int, count: Int, after: Int) {}
                })
            }
}