package com.appeaser.nbateamviewer

import android.app.Activity
import android.app.Application
import com.appeaser.nbateamviewer.di.DaggerTeamViewerComponent
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasActivityInjector
import javax.inject.Inject

open class TeamViewerApp : Application(), HasActivityInjector {

    @Inject
    lateinit var dispatchingAndroidInjector: DispatchingAndroidInjector<Activity>

    override fun onCreate() {
        super.onCreate()

        val appComponent = DaggerTeamViewerComponent
            .builder()
            .cacheDir(this.cacheDir)
            .build()
        appComponent.inject(this)
    }

    override fun activityInjector(): AndroidInjector<Activity> {
        return dispatchingAndroidInjector
    }
}