/*
 * Copyright 2018, The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy factoryOf the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package maxeem.america.util

import android.view.View
import android.widget.TextView
import androidx.appcompat.widget.TooltipCompat
import androidx.databinding.BindingAdapter
import maxeem.america.gdg.R
import org.jetbrains.anko.design.longSnackbar

@BindingAdapter("visibleOn")
fun View.visibleOn(condition: Boolean?) {
    visibility = if (condition == true) View.VISIBLE else View.INVISIBLE
}

@BindingAdapter("textHtml")
fun TextView.textHtml(str: String) {
    text = str.fromHtml()
}

@BindingAdapter("tooltipCompat")
fun View.tooltipCompat(str: String) {
    TooltipCompat.setTooltipText(this, str)
}

@BindingAdapter("onClickNotImplemented")
fun View.onClickNotImplemented(msg: CharSequence?) {
    onClick {
        longSnackbar(R.string.not_implemented).apply {
            anchorView = this@onClickNotImplemented
        }
    }
}
