package com.appeaser.nbateamviewer.presentation.teamlist

import com.appeaser.nbateamviewer.domain.entity.SortBy
import com.appeaser.nbateamviewer.domain.entity.Team

interface TeamListContract {

    interface View {

        fun showLoading()

        fun hideLoading()

        fun showTeams(teams: List<Team>)

        fun showConnectionIssueErrorMessage()

        fun showHttpFailureErrorMessage()

        fun showGenericErrorMessage()
    }

    interface Presenter {

        fun attachView(view: View)

        fun sortTeamListBy(newSortBy: SortBy)

        fun detachView()
    }
}