package com.appeaser.nbateamviewer.presentation.teamlist

import android.util.Log
import com.appeaser.nbateamviewer.domain.entity.SortBy
import com.appeaser.nbateamviewer.domain.entity.Team
import com.appeaser.nbateamviewer.domain.usecase.GetAllTeamsUseCase
import io.reactivex.Scheduler
import io.reactivex.disposables.Disposable

class TeamListPresenter(
    private val getAllTeamsUseCase: GetAllTeamsUseCase,
    private val executeScheduler: Scheduler,
    private val postExecuteScheduler: Scheduler
) : TeamListContract.Presenter {

    private var disposable: Disposable? = null
    private lateinit var teamListView: TeamListContract.View
    private lateinit var teamList: List<Team>

    // default sorting order
    private var sortBy = SortBy.ALPHABETICAL_ASC

    override fun attachView(view: TeamListContract.View, sortByFromState: SortBy?) {
        teamListView = view
        sortByFromState?.apply { sortBy = this }
        loadTeamList()
    }

    /**
     * Updates [SortBy] preference, and refreshes
     * the view iff [teamList] is available(initialized).
     * Else, it tries to fetch the team list by
     * executing [GetAllTeamsUseCase].
     *
     * [newSortBy] new sort preference
     */
    override fun sortTeamListBy(newSortBy: SortBy) {
        // early exit
        if (::teamList.isInitialized && sortBy == newSortBy) return

        sortBy = newSortBy

        if (::teamList.isInitialized) {
            teamList = applySortPreference(teamList)
            teamListView.showTeams(teamList)
            return
        }

        loadTeamList()
    }

    private fun loadTeamList() {
        // use case is currently executing
        if (disposable?.isDisposed == false) return

        getAllTeamsUseCase.execute()
            .subscribeOn(executeScheduler)
            .observeOn(postExecuteScheduler)
            .doOnSubscribe {
                teamListView.showLoading()
            }.subscribe({
                handleSuccess(it)
            }, {
                handleError(it)
            }).apply {
                disposable = this
            }
    }

    /**
     * Updates [TeamListContract.View] after applying
     * the sorting preference.
     */
    private fun handleSuccess(teams: List<Team>) {
        teamList = applySortPreference(teams)
        teamListView.hideLoading()
        teamListView.showTeams(teamList)
    }

    private fun applySortPreference(teams: List<Team>): List<Team> {
        return when (sortBy) {
            SortBy.ALPHABETICAL_ASC -> teams.sortedBy { it.fullName }
            SortBy.ALPHABETICAL_DESC -> teams.sortedByDescending { it.fullName }
            SortBy.WINS_ASC -> teams.sortedBy { it.wins }
            SortBy.WINS_DESC -> teams.sortedByDescending { it.wins }
            SortBy.LOSSES_ASC -> teams.sortedBy { it.losses }
            SortBy.LOSSES_DESC -> teams.sortedByDescending { it.losses }
        }
    }

    /**
     * Asks [TeamListContract.View] to display
     * appropriate error message to the user.
     */
    private fun handleError(error: Throwable?) {
        Log.e(TAG, "Error while fetching teams list", error)

        teamListView.hideLoading()

        when (error) {
            is GetAllTeamsUseCase.Exception.ConnectionIssueException -> teamListView.showConnectionIssueErrorMessage()
            is GetAllTeamsUseCase.Exception.HttpFailureException -> teamListView.showHttpFailureErrorMessage()
            else /* [GenericException] + unhandled exceptions */ -> teamListView.showGenericErrorMessage()
        }
    }

    override fun getCurrentSortByPreference() = sortBy

    override fun detachView() {
        disposable?.dispose()
    }

    companion object {
        private const val TAG = "TeamListPresenter"
    }
}