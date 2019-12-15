package maxeem.america

import android.app.Application
import maxeem.america.gdg.network.GdgApi
import maxeem.america.gdg.repository.GdgRepository
import maxeem.america.gdg.repository.GdgRepositoryImpl
import maxeem.america.util.hash
import maxeem.america.util.pid
import maxeem.america.util.timeMillis
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.dsl.module

val app = App.instance

private val appModule = module {
    single { GdgApi.service }
    single<GdgRepository> { GdgRepositoryImpl() }
}

class App : Application(), AnkoLogger {

    companion object {
        lateinit var instance: App
            private set
    }

    init {
        info("$pid - $hash $timeMillis init")
        instance = this
    }

    override fun onCreate() { super.onCreate()
        startKoin {
            androidLogger(); androidContext(this@App)
            modules(appModule)
        }
    }

}
