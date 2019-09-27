package maxeem.america.gdg.ui

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.net.toUri
import androidx.navigation.fragment.findNavController
import maxeem.america.app
import maxeem.america.base.BaseFragment
import maxeem.america.gdg.R
import maxeem.america.gdg.databinding.FragmentAboutBinding
import maxeem.america.packageInfo
import maxeem.america.util.asString
import maxeem.america.util.hash
import maxeem.america.util.onClick
import maxeem.america.util.timeMillis
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info

class AboutFragment : BaseFragment(), AnkoLogger {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?)
        = FragmentAboutBinding.inflate(inflater, container, false).apply {
            info("$hash $timeMillis onCreateView, savedInstanceState: $savedInstanceState")
            lifecycleOwner = viewLifecycleOwner
            toolbar.setNavigationOnClickListener {
                findNavController().navigateUp()
            }
            author.apply {
                val mail = Intent(Intent.ACTION_SENDTO)
                        .setData("mailto:${R.string.author_email.asString()}".toUri())
                isClickable = mail.resolveActivity(app.packageManager) != null
                if (isClickable) onClick {
                    startActivity(mail.apply {
                        putExtra(Intent.EXTRA_SUBJECT, R.string.email_subject.asString(R.string.app_name.asString()))
                    })
                }
            }
            version.text = app.packageInfo.versionName//.substringBefore('-')
            googlePlay.onClick {
                Intent(Intent.ACTION_VIEW).apply {
                    data = "https://play.google.com/store/apps/details?id=${app.packageName}".toUri()
                    `package` = "com.android.vending"
                    if (resolveActivity(app.packageManager) == null)
                        `package` = null
                    startActivity(this)
                }
            }
        }.root

}