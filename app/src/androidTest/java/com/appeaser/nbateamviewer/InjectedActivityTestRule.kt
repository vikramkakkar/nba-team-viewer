package com.appeaser.nbateamviewer

import android.app.Activity
import androidx.test.espresso.intent.rule.IntentsTestRule
import androidx.test.platform.app.InstrumentationRegistry
import dagger.android.AndroidInjector

/**
 * Allows dagger injection in Activity tests.
 */
class InjectedActivityTestRule<T : Activity>(
    private val activityClassVal: Class<T>,
    private val activityInjector: (T) -> Unit
) : IntentsTestRule<T>(
    activityClassVal,
    false,
    false
) {
    override fun beforeActivityLaunched() {
        super.beforeActivityLaunched()

        val testApp = InstrumentationRegistry.getInstrumentation().targetContext.applicationContext as TestApplication
        testApp.actInjector = AndroidInjector {
            if (activityClassVal.canonicalName == it::class.java.canonicalName) {
                // checked
                @Suppress("UNCHECKED_CAST")
                activityInjector(it as T)
            }
        }
    }
}