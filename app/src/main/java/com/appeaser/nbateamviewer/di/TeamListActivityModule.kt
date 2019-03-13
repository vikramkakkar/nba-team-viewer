package com.appeaser.nbateamviewer.di

import com.appeaser.nbateamviewer.domain.usecase.GetAllTeamsUseCase
import com.appeaser.nbateamviewer.presentation.teamlist.TeamListContract
import com.appeaser.nbateamviewer.presentation.teamlist.TeamListPresenter
import dagger.Module
import dagger.Provides
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

@Module
class TeamListActivityModule {

    @ActivityScope
    @Provides
    fun teamListPresenter(getAllTeamsUseCase: GetAllTeamsUseCase): TeamListContract.Presenter {
        return TeamListPresenter(getAllTeamsUseCase, Schedulers.io(), AndroidSchedulers.mainThread())
    }
}