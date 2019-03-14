package com.appeaser.nbateamviewer.di

import com.appeaser.nbateamviewer.domain.usecase.GetTeamByIdUseCase
import com.appeaser.nbateamviewer.presentation.teamviewer.TeamViewerContract
import com.appeaser.nbateamviewer.presentation.teamviewer.TeamViewerPresenter
import dagger.Module
import dagger.Provides
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

@Module
class TeamViewerActivityModule {

    @ActivityScope
    @Provides
    fun teamViewerPresenter(getTeamByIdUseCase: GetTeamByIdUseCase): TeamViewerContract.Presenter {
        return TeamViewerPresenter(getTeamByIdUseCase, Schedulers.io(), AndroidSchedulers.mainThread())
    }
}