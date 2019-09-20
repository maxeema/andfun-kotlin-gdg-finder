package maxeem.america.gdg.misc

import android.view.View
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import maxeem.america.gdg.network.GdgChapter
import maxeem.america.gdg.search.GdgListAdapter

@BindingAdapter("listData")
fun RecyclerView.listData(data: List<GdgChapter>?) {
    (adapter as GdgListAdapter).submitList(data) {
        scrollToPosition(0)
    }
}

@BindingAdapter("showOnlyWhenEmpty")
fun View.showOnlyWhenEmpty(data: List<GdgChapter>?) {
    visibility = when {
        data.isNullOrEmpty() -> View.VISIBLE
        else -> View.GONE
    }
}
