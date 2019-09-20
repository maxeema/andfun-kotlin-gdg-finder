package maxeem.america

import android.app.Application
import android.os.Handler
import maxeem.america.util.hash
import maxeem.america.util.pid
import maxeem.america.util.timeMillis
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info

val app = App.instance

class App : Application(), AnkoLogger {

    companion object {
        private lateinit var _instance: App
        val instance get() = _instance
    }

    init
    {
        info("$pid - $hash $timeMillis init")
        _instance = this
    }

}

val App.handler by lazy { Handler(app.mainLooper) }
val App.packageInfo
    get() = packageManager.getPackageInfo(packageName, 0)
