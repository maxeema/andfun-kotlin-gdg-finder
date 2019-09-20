package maxeem.america.base

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import maxeem.america.util.hash
import maxeem.america.util.timeMillis
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info

open class BaseFragment : Fragment(), AnkoLogger {

    val viewOwner get() = runCatching { viewLifecycleOwner }.getOrNull()

    override fun onCreate(savedInstanceState: Bundle?) { super.onCreate(savedInstanceState)
        info("$hash $timeMillis onCreate, savedInstanceState: $savedInstanceState")
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) { super.onViewCreated(view, savedInstanceState)
        info("$hash $timeMillis onViewCreated, savedInstanceState: $savedInstanceState")
    }
    override fun onSaveInstanceState(outState: Bundle) { super.onSaveInstanceState(outState)
        info("$hash $timeMillis onSaveInstanceState")
    }
    override fun onViewStateRestored(savedInstanceState: Bundle?) { super.onViewStateRestored(savedInstanceState)
        info("$hash $timeMillis onViewStateRestored, savedInstanceState: $savedInstanceState")
    }
    override fun onDestroyView() { super.onDestroyView()
        info("$hash $timeMillis onDestroyView")
    }
    override fun onDestroy() { super.onDestroy()
        info("$hash $timeMillis onDestroy")
    }
    override fun onAttach(context: Context) { super.onAttach(context)
        info("$hash $timeMillis onAttach, $context")
    }
    override fun onDetach() { super.onDetach()
        info("$hash $timeMillis onDetach")
    }
    override fun onStart() { super.onStart()
        info("$hash $timeMillis onStart")
    }
    override fun onStop() { super.onStop()
        info("$hash $timeMillis onStop")
    }
    override fun onPause() { super.onPause()
        info("$hash $timeMillis onPause")
    }
    override fun onResume() { super.onResume()
        info("$hash $timeMillis onResume")
    }

}