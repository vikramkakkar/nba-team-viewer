package com.appeaser.nbateamviewer

import android.app.Activity
import dagger.android.AndroidInjector

class TestApplication : TeamViewerApp() {

    lateinit var actInjector: AndroidInjector<Activity>

    override fun activityInjector(): AndroidInjector<Activity> {
        return actInjector
    }
}