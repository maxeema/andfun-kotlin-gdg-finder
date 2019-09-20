package maxeem.america.util

import android.view.View
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import maxeem.america.app

/**
 * Extensions
 */

val Any.hash get() = hashCode()

fun View.onClick(l: ()->Unit) = setOnClickListener { l() }

fun Fragment.compatActivity() = activity as AppCompatActivity?

fun Fragment.materialAlert(msg: CharSequence, code: (MaterialAlertDialogBuilder.()->Unit)? = null)
        = MaterialAlertDialogBuilder(context).apply {
    setMessage(msg)
    code?.invoke(this)
}.show()
fun Fragment.materialAlert(@StringRes msg: Int, code: (MaterialAlertDialogBuilder.()->Unit)? = null)
        = materialAlert(app.getString(msg), code)
