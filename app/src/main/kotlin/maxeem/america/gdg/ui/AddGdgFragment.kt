package maxeem.america.gdg.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.core.util.PatternsCompat
import androidx.core.widget.doOnTextChanged
import androidx.databinding.ObservableBoolean
import androidx.fragment.app.viewModels
import androidx.lifecycle.observe
import androidx.navigation.fragment.findNavController
import com.google.android.material.textfield.TextInputLayout
import maxeem.america.base.BaseFragment
import maxeem.america.gdg.R
import maxeem.america.gdg.databinding.FragmentAddGdgBinding
import maxeem.america.gdg.misc.Conf
import maxeem.america.gdg.viewmodels.AddGdgViewModel
import maxeem.america.util.asString
import org.jetbrains.anko.design.longSnackbar
import org.jetbrains.anko.design.snackbar
import org.jetbrains.anko.info
import java.util.regex.Pattern

class AddGdgFragment : BaseFragment() {

    private companion object {
        private val EMAIL      = PatternsCompat.EMAIL_ADDRESS
        private val MIN_LENGTH = Pattern.compile(".{3,}")
        //
        const val STATE_KEY_SENT = "sent"
    }

    private val model : AddGdgViewModel by viewModels()
    private var sent = ObservableBoolean()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?)
    = FragmentAddGdgBinding.inflate(inflater).apply {
        lifecycleOwner = viewOwner
        viewModel = model
        hasSent = sent

        sent.set(savedInstanceState?.getBoolean(STATE_KEY_SENT) ?: false)

        toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }

        val inputs = arrayOf(editTextNameLayout, editTextEmailLayout, editTextCityLayout, editTextCountryLayout, editTextRegionLayout)
        inputs.forEach {
            it.isErrorEnabled = false
            it.editText?.doOnTextChanged { text: CharSequence?, _: Int, _: Int, _: Int ->
                if (!it.error.isNullOrBlank())
                    validate(text, it, if (it == editTextEmailLayout) EMAIL else MIN_LENGTH)
            }
        }

        model.submitEvent.observe(viewLifecycleOwner) {
            if (it != true) return@observe
            model.consumeSubmitEvent()
            if (sent.get()) {
                findNavController().navigateUp()
                return@observe
            }
            if (!validate(inputs)) {
                view?.snackbar(R.string.fill_first)?.apply {
                    anchorView = submitBtn
                }
                return@observe
            }
            view?.longSnackbar(R.string.application_submitted)
            //
            sent.set(true)
            checkSent(this, inputs)
        }
        editTextRegion.setAdapter(ArrayAdapter<String>(context!!,
                R.layout.region_autocomplete_item, Conf.GDG.REGIONS))

        checkSent(this, inputs)
    }.root

    private fun checkSent(binding: FragmentAddGdgBinding, inputs: Array<TextInputLayout>) {
        if (sent.get()) with (binding) {
            submitBtn.text = R.string.done.asString()
            submitBtn.contentDescription = R.string.submitted.asString()
            (inputs + editTextWhyLayout).forEach { text ->
                text.editText?.isEnabled = false
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putBoolean(STATE_KEY_SENT, sent.get())
        super.onSaveInstanceState(outState)
    }

    private fun validate(text: CharSequence?, inputTextLayout: TextInputLayout, pattern: Pattern? = null) : Boolean {
        info("validate: $text, pattern: $pattern")
        inputTextLayout.error = when {
            text.isNullOrBlank() -> R.string.field_must_not_be_empty.asString(inputTextLayout.hint!!)
            pattern != null && !pattern.matcher(text.trim()).matches() -> R.string.field_is_not_valid.asString(inputTextLayout.hint!!)
            else -> null
        }
        return inputTextLayout.error.isNullOrBlank()
    }

    private fun validate(inputs: Array<TextInputLayout>) : Boolean {
        var valid = true
        inputs.forEach {
            val v = validate(it.editText?.text, it, if (it.id == R.id.editTextEmailLayout) EMAIL else MIN_LENGTH)
            if (valid)
                valid = v
        }
        return valid
    }

}
