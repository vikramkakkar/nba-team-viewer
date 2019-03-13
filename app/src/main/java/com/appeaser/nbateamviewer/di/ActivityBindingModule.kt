package com.appeaser.nbateamviewer.di

import com.appeaser.nbateamviewer.presentation.teamlist.TeamListActivity
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Suppress("unused")
@Module
abstract class ActivityBindingModule {

    @ActivityScope
    @ContributesAndroidInjector(modules = [TeamListActivityModule::class])
    abstract fun teamListActivity(): TeamListActivity
}