package com.appeaser.nbateamviewer.di

import com.appeaser.nbateamviewer.presentation.teamlist.TeamListActivity
import com.appeaser.nbateamviewer.presentation.teamviewer.TeamViewerActivity
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Suppress("unused")
@Module
abstract class ActivityBindingModule {

    @ActivityScope
    @ContributesAndroidInjector(modules = [TeamListActivityModule::class])
    abstract fun teamListActivity(): TeamListActivity

    @ActivityScope
    @ContributesAndroidInjector(modules = [TeamViewerActivityModule::class])
    abstract fun teamViewerActivity(): TeamViewerActivity
}