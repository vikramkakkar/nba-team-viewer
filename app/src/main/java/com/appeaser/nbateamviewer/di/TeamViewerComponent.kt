package com.appeaser.nbateamviewer.di

import com.appeaser.nbateamviewer.TeamViewerApp
import dagger.BindsInstance
import dagger.Component
import dagger.android.support.AndroidSupportInjectionModule
import java.io.File

@ApplicationScope
@Component(
    modules = [
        TeamViewerAppModule::class,
        AndroidSupportInjectionModule::class,
        ActivityBindingModule::class]
)
interface TeamViewerComponent {

    fun inject(app: TeamViewerApp)

    @Component.Builder
    interface Builder {
        fun build(): TeamViewerComponent

        @BindsInstance
        fun cacheDir(@CacheDir cacheDir: File): Builder
    }
}