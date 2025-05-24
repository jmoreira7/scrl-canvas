package com.jmoreira7.scrlcanvas

import android.app.Application
import com.jmoreira7.scrlcanvas.data.AppostropheApiManager
import com.jmoreira7.scrlcanvas.ui.MainViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

class ScrlCanvasApplication : Application() {
    private val appModule = module {
        singleOf(::AppostropheApiManager)
        viewModelOf(::MainViewModel)
    }

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidLogger()
            androidContext(this@ScrlCanvasApplication)
            modules(appModule)
        }
    }
}