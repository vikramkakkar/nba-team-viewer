package com.appeaser.nbateamviewer.testdoubles

import com.appeaser.nbateamviewer.domain.entity.Team
import com.appeaser.nbateamviewer.domain.usecase.GetTeamByIdUseCase
import io.reactivex.Single
import io.reactivex.subjects.SingleSubject

open class GetTeamByIdUseCaseDummy : GetTeamByIdUseCase {
    override fun execute(teamId: Int): Single<Team> {
        return Single.create<Team> { }.doOnSubscribe {
            throw NotImplementedError("invocation on dummy instance")
        }
    }
}

class GetTeamByIdUseCaseFake : GetTeamByIdUseCaseDummy() {

    private lateinit var single: SingleSubject<Team>

    override fun execute(teamId: Int): Single<Team> {
        single = SingleSubject.create<Team>()
        return single
    }

    fun respondWithSuccess(team: Team) {
        single.onSuccess(team)
    }

    fun respondWithError(error: Throwable) {
        single.onError(error)
    }

    fun isDisposed(): Boolean {
        return single.hasObservers().not()
    }
}