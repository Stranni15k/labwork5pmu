package ru.ulstu.`is`.pmu

import android.app.Application
import ru.ulstu.`is`.pmu.common.AppContainer
import ru.ulstu.`is`.pmu.common.AppDataContainer

class TaskApplication : Application() {
    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        container = AppDataContainer(this)
    }
}