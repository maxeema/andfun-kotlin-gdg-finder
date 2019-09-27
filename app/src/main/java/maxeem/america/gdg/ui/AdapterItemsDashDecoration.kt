package maxeem.america.gdg.ui

import android.graphics.Canvas
import android.graphics.DashPathEffect
import android.graphics.Paint
import android.graphics.Rect
import android.view.View
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import androidx.core.view.children
import androidx.recyclerview.widget.RecyclerView
import maxeem.america.app
import org.jetbrains.anko.dip

class AdapterItemsDashDecoration(@ColorRes color: Int,
                                 private val adapterFirstItemRealIdx: Int = 0) : RecyclerView.ItemDecoration() {

    private companion object {
        const val TAG_FOOTER = "footer"
        //
        val LINE_SIZE = app.dip(1)
        val PATH_INTERVALS = floatArrayOf(app.dip(2).toFloat(), app.dip(4).toFloat())
        val IGNORED_BOTTOM_REMINDER = app.dip(15)
        val SKIPPED_HORIZONTAL_OFFSET = app.dip(5).toFloat()
    }

    private val mPaint = Paint().apply{
        this.pathEffect = DashPathEffect(PATH_INTERVALS, 0f)
        this.color = ContextCompat.getColor(app, color)
        this.strokeWidth = LINE_SIZE.toFloat()
    }
    private val mBounds = Rect()

    override fun onDraw(canvas: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        with (canvas) {
            save()
            draw(this, parent)
            restore()
        }
    }

    private fun draw(c: Canvas, r: RecyclerView) {
        val start: Float; val end: Float

        if (r.clipToPadding) {
            start = maxOf(SKIPPED_HORIZONTAL_OFFSET, r.paddingLeft.toFloat())
            end = r.width.toFloat() - maxOf(SKIPPED_HORIZONTAL_OFFSET, r.paddingRight.toFloat())
            c.clipRect(start, r.paddingTop.toFloat(), end, r.height.toFloat() - r.paddingBottom)
        } else {
            start = SKIPPED_HORIZONTAL_OFFSET
            end = -SKIPPED_HORIZONTAL_OFFSET + r.width.toFloat()
        }

        var bottom = 0f

        for (child in r.children) {
            r.getDecoratedBoundsWithMargins(child, mBounds)
            bottom = mBounds.bottom + child.translationY
            if (bottom + IGNORED_BOTTOM_REMINDER >= r.height)
                return
            c.drawLine(start, bottom, end, bottom, mPaint)
        }

        val childHeight = r.getChildAt(adapterFirstItemRealIdx)?.height?.takeIf { it > 0 } ?: return
        while (true) {
            bottom += childHeight
            if (bottom + IGNORED_BOTTOM_REMINDER >= r.height)
                return
            c.drawLine(start, bottom, end, bottom, mPaint)
        }
    }

    override fun getItemOffsets(rect: Rect, view: View, recycler: RecyclerView, st: RecyclerView.State) {
        if (view.tag == TAG_FOOTER) {
            rect.set(0, 0, 0, recycler.getChildAt(adapterFirstItemRealIdx).height)
        } else {
            rect.set(0, 0, 0, LINE_SIZE)
        }
    }

}
