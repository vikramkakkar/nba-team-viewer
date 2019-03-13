package com.appeaser.nbateamviewer.presentation.teamlist

import com.appeaser.nbateamviewer.SUCCESS_RESPONSE_BODY
import com.appeaser.nbateamviewer.domain.entity.SortBy
import com.appeaser.nbateamviewer.domain.entity.Team
import com.appeaser.nbateamviewer.domain.usecase.GetAllTeamsUseCase
import com.appeaser.nbateamviewer.fromJson
import com.appeaser.nbateamviewer.testdoubles.GetAllTeamsUseCaseExceptionStub
import com.appeaser.nbateamviewer.testdoubles.GetAllTeamsUseCaseFake
import com.nhaarman.mockitokotlin2.*
import io.reactivex.schedulers.Schedulers
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.net.SocketTimeoutException

class TeamListPresenterTest {

    private val getAllTeamsUseCase = GetAllTeamsUseCaseFake()
    private val scheduler = Schedulers.trampoline()
    private val presenter = TeamListPresenter(getAllTeamsUseCase, scheduler, scheduler)

    private val view: TeamListContract.View = mock()

    @Test
    fun `show loading view on attach`() {
        presenter.attachView(view)

        verify(view).showLoading()
    }

    @Test
    fun `team list fetched successfully - hide loading`() {
        val teamList = fromJson<List<Team>>(SUCCESS_RESPONSE_BODY)
        presenter.attachView(view)

        getAllTeamsUseCase.respondWithSuccess(teamList)

        verify(view).showTeams(teamList)
        verify(view).hideLoading()
    }

    @Test
    fun `dispose on detach`() {
        presenter.attachView(view)

        presenter.detachView()

        assertTrue(getAllTeamsUseCase.isDisposed())
    }

    @Test
    fun `hide loading on error`() {
        presenter.attachView(view)

        getAllTeamsUseCase.respondWithError(GetAllTeamsUseCase.Exception.ConnectionIssueException(SocketTimeoutException()))

        verify(view).hideLoading()
    }

    @Test
    fun `show connection issue error message`() {
        presenter.attachView(view)

        getAllTeamsUseCase.respondWithError(GetAllTeamsUseCase.Exception.ConnectionIssueException(SocketTimeoutException()))

        verify(view).showConnectionIssueErrorMessage()
    }

    @Test
    fun `show http failure error message`() {
        presenter.attachView(view)

        getAllTeamsUseCase.respondWithError(GetAllTeamsUseCase.Exception.HttpFailureException(Throwable(), false))

        verify(view).showHttpFailureErrorMessage()
    }

    @Test
    fun `show generic failure error message`() {
        presenter.attachView(view)

        getAllTeamsUseCase.respondWithError(GetAllTeamsUseCase.Exception.GenericException(KotlinNullPointerException()))

        verify(view).showGenericErrorMessage()
    }

    @Test
    fun `show generic failure error message on unhandled error`() {
        val getAllTeamsUseCase = GetAllTeamsUseCaseExceptionStub()
        val presenter = TeamListPresenter(getAllTeamsUseCase, scheduler, scheduler)

        presenter.attachView(view)

        verify(view).showGenericErrorMessage()
    }

    @Test
    fun `sort alphabetically by default`() {
        presenter.attachView(view)
        getAllTeamsUseCase.respondWithSuccess(unsortedTeamList)

        val argCaptor = argumentCaptor<List<Team>>()
        verify(view).showTeams(argCaptor.capture())

        val sortedByNameAsc = unsortedTeamList.sortedBy { it.fullName }
        assertEquals(sortedByNameAsc, argCaptor.lastValue)
    }

    @Test
    fun `sort alphabetically desc`() {
        presenter.attachView(view)

        presenter.sortTeamListBy(SortBy.ALPHABETICAL_DESC)
        getAllTeamsUseCase.respondWithSuccess(unsortedTeamList)

        val argCaptor = argumentCaptor<List<Team>>()
        verify(view).showTeams(argCaptor.capture())

        val sortedByNameAsc = unsortedTeamList.sortedByDescending { it.fullName }
        assertEquals(sortedByNameAsc, argCaptor.lastValue)
    }

    @Test
    fun `sort by wins ascending`() {
        presenter.attachView(view)

        presenter.sortTeamListBy(SortBy.WINS_ASC)
        getAllTeamsUseCase.respondWithSuccess(unsortedTeamList)

        val argCaptor = argumentCaptor<List<Team>>()
        verify(view).showTeams(argCaptor.capture())

        val sortedByWinsAsc = unsortedTeamList.sortedBy { it.wins }
        assertEquals(sortedByWinsAsc, argCaptor.lastValue)
    }

    @Test
    fun `sort by wins descending`() {
        presenter.attachView(view)

        presenter.sortTeamListBy(SortBy.WINS_DESC)
        getAllTeamsUseCase.respondWithSuccess(unsortedTeamList)

        val argCaptor = argumentCaptor<List<Team>>()
        verify(view).showTeams(argCaptor.capture())

        val sortedByWinsAsc = unsortedTeamList.sortedByDescending { it.wins }
        assertEquals(sortedByWinsAsc, argCaptor.lastValue)
    }

    @Test
    fun `sort by losses ascending`() {
        presenter.attachView(view)

        presenter.sortTeamListBy(SortBy.LOSSES_ASC)
        getAllTeamsUseCase.respondWithSuccess(unsortedTeamList)

        val argCaptor = argumentCaptor<List<Team>>()
        verify(view).showTeams(argCaptor.capture())

        val sortedByWinsAsc = unsortedTeamList.sortedBy { it.losses }
        assertEquals(sortedByWinsAsc, argCaptor.lastValue)
    }

    @Test
    fun `sort by losses descending`() {
        presenter.attachView(view)

        presenter.sortTeamListBy(SortBy.LOSSES_DESC)
        getAllTeamsUseCase.respondWithSuccess(unsortedTeamList)

        val argCaptor = argumentCaptor<List<Team>>()
        verify(view).showTeams(argCaptor.capture())

        val sortedByWinsAsc = unsortedTeamList.sortedByDescending { it.losses }
        assertEquals(sortedByWinsAsc, argCaptor.lastValue)
    }

    @Test
    fun `sort by same criterion`() {
        presenter.attachView(view)

        presenter.sortTeamListBy(SortBy.WINS_ASC)
        getAllTeamsUseCase.respondWithSuccess(unsortedTeamList)

        // trigger [sortTeamListBy] with the same criterion again
        presenter.sortTeamListBy(SortBy.WINS_ASC)

        // [showTeams()] should be called once if the
        // sort criterion is the same as before
        verify(view, times(1)).showTeams(any())
    }

    @Test
    fun `sort by same criterion when team list is uninitialized - should execute use case`() {
        presenter.attachView(view)

        presenter.sortTeamListBy(SortBy.WINS_ASC)
        getAllTeamsUseCase.respondWithError(Throwable())

        // trigger [sortTeamListBy] with the same criterion again
        presenter.sortTeamListBy(SortBy.WINS_ASC)

        // [sortTeamListBy()] should handle this case
        // by executing [GetAllTeamsUseCase] again.

        getAllTeamsUseCase.respondWithSuccess(unsortedTeamList)

        val argCaptor = argumentCaptor<List<Team>>()
        verify(view).showTeams(argCaptor.capture())

        val sortedByWinsAsc = unsortedTeamList.sortedBy { it.wins }

        assertEquals(sortedByWinsAsc, argCaptor.lastValue)
    }

    @Test
    fun `sort by called when fetch failed`() {
        presenter.attachView(view)
        getAllTeamsUseCase.respondWithError(Throwable())

        // Since we responded with an error,
        // team list is still uninitialized.

        presenter.sortTeamListBy(SortBy.WINS_ASC)

        // [sortTeamListBy()] should handle this case
        // by executing [GetAllTeamsUseCase] again.

        getAllTeamsUseCase.respondWithSuccess(unsortedTeamList)

        val argCaptor = argumentCaptor<List<Team>>()
        verify(view).showTeams(argCaptor.capture())

        val sortedByWinsAsc = unsortedTeamList.sortedBy { it.wins }

        assertEquals(sortedByWinsAsc, argCaptor.lastValue)
    }

    @Test
    fun `sort by called when fetch is in progress`() {
        presenter.attachView(view)

        // Team list is uninitialized because
        // [GetAllTeamsUseCase] hasn't responded yet.

        presenter.sortTeamListBy(SortBy.WINS_ASC)

        // [sortTeamListBy()] should handle this case
        // by waiting for [GetAllTeamsUseCase] to finish.

        getAllTeamsUseCase.respondWithSuccess(unsortedTeamList)

        val argCaptor = argumentCaptor<List<Team>>()
        verify(view).showTeams(argCaptor.capture())

        val sortedByWinsAsc = unsortedTeamList.sortedBy { it.wins }

        assertEquals(sortedByWinsAsc, argCaptor.lastValue)
    }

    @Test
    fun `sort by passed while attaching`() {
        val sortByPreference = SortBy.WINS_ASC

        presenter.attachView(view, sortByPreference)
        getAllTeamsUseCase.respondWithSuccess(unsortedTeamList)

        val argCaptor = argumentCaptor<List<Team>>()
        verify(view).showTeams(argCaptor.capture())

        val sortedByWinsAsc = unsortedTeamList.sortedBy { it.wins }

        assertEquals(sortedByWinsAsc, argCaptor.lastValue)
    }

    private val unsortedTeamList = listOf(
        Team(1, "z", 50, 25, listOf()),
        Team(2, "o", 100, 75, listOf()),
        Team(3, "a", 75, 50, listOf())
    )
}