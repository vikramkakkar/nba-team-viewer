package com.appeaser.nbateamviewer.presentation.teamviewer

import com.appeaser.nbateamviewer.TEST_TEAM
import com.appeaser.nbateamviewer.domain.usecase.GetTeamByIdUseCase
import com.appeaser.nbateamviewer.testdoubles.GetTeamByIdUseCaseFake
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import io.reactivex.schedulers.Schedulers
import org.junit.Assert.assertTrue
import org.junit.Test

class TeamViewerPresenterTest {

    private val getTeamByIdUseCase = GetTeamByIdUseCaseFake()
    private val scheduler = Schedulers.trampoline()
    private val presenter = TeamViewerPresenter(getTeamByIdUseCase, scheduler, scheduler)

    private val view: TeamViewerContract.View = mock()
    private val teamId = 1

    @Test
    fun `show loading view on attach`() {
        presenter.attachView(view, teamId)

        verify(view).showLoading()
    }

    @Test
    fun `team fetched successfully - hide loading`() {
        val team = TEST_TEAM
        presenter.attachView(view, team.id)

        getTeamByIdUseCase.respondWithSuccess(team)

        verify(view).showTeam(team)
        verify(view).hideLoading()
    }

    @Test
    fun `dispose on detach`() {
        presenter.attachView(view, teamId)

        presenter.detachView()

        assertTrue(getTeamByIdUseCase.isDisposed())
    }

    @Test
    fun `finish up on error`() {
        presenter.attachView(view, teamId)

        getTeamByIdUseCase.respondWithError(GetTeamByIdUseCase.Exception.NotFoundException())

        verify(view).finishUpWithError()
    }
}