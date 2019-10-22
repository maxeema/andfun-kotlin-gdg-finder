package maxeem.america.gdg.misc

import android.view.View
import androidx.databinding.BindingAdapter

@BindingAdapter("showOnlyWhenNotEmpty")
fun View.showOnlyWhenNotEmpty(data: List<Any>?) {
    visibility = when {
        data.isNullOrEmpty() -> View.GONE
        else -> View.VISIBLE
    }
}
@BindingAdapter("showOnlyWhenEmpty")
fun View.showOnlyWhenEmpty(data: List<Any>?) {
    visibility = when {
        data.isNullOrEmpty() -> View.VISIBLE
        else -> View.GONE
    }
}
