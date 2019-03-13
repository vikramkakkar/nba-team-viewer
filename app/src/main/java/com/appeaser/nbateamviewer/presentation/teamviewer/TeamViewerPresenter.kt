package com.appeaser.nbateamviewer.presentation.teamviewer

import android.util.Log
import com.appeaser.nbateamviewer.domain.entity.Team
import com.appeaser.nbateamviewer.domain.usecase.GetTeamByIdUseCase
import io.reactivex.Scheduler
import io.reactivex.disposables.Disposable

class TeamViewerPresenter(
    private val getTeamByIdUseCase: GetTeamByIdUseCase,
    private val executeScheduler: Scheduler,
    private val postExecuteScheduler: Scheduler
) : TeamViewerContract.Presenter {

    private lateinit var teamViewerView: TeamViewerContract.View

    private var disposable: Disposable? = null

    override fun attachView(view: TeamViewerContract.View, teamId: Int) {
        teamViewerView = view
        loadTeam(teamId)
    }

    private fun loadTeam(teamId: Int) {
        getTeamByIdUseCase.execute(teamId)
            .subscribeOn(executeScheduler)
            .observeOn(postExecuteScheduler)
            .doOnSubscribe {
                teamViewerView.showLoading()
            }.subscribe({
                handleSuccess(it)
            }, {
                handleError(it)
            }).apply {
                disposable = this
            }
    }

    private fun handleSuccess(team: Team) {
        Log.i(TAG, "Team fetched successfully")

        teamViewerView.hideLoading()
        teamViewerView.showTeam(team)
    }

    private fun handleError(error: Throwable?) {
        Log.e(TAG, "Error while fetching team", error)
        teamViewerView.finishUpWithError()
    }

    override fun detachView() {
        disposable?.dispose()
    }

    companion object {
        private const val TAG = "TeamViewerPresenter"
    }
}