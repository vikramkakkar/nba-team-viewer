package com.appeaser.nbateamviewer.presentation.teamviewer

import com.appeaser.nbateamviewer.domain.entity.Team

interface TeamViewerContract {

    interface View {

        fun showLoading()

        fun hideLoading()

        fun showTeam(team: Team)

        fun finishUpWithError()
    }

    interface Presenter {

        fun attachView(view: View, teamId: Int)

        fun detachView()
    }
}