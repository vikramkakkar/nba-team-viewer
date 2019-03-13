package com.appeaser.nbateamviewer.testdoubles

import com.appeaser.nbateamviewer.domain.entity.Team
import com.appeaser.nbateamviewer.domain.usecase.GetAllTeamsUseCase
import io.reactivex.Single
import io.reactivex.subjects.SingleSubject

open class GetAllTeamsUseCaseDummy : GetAllTeamsUseCase {
    override fun execute(): Single<List<Team>> {
        return Single.create<List<Team>> { }.doOnSubscribe {
            throw NotImplementedError("invocation on dummy instance")
        }
    }
}

/**
 * Allows controlled emission of success/error values.
 */
class GetAllTeamsUseCaseFake : GetAllTeamsUseCaseDummy() {

    private lateinit var single: SingleSubject<List<Team>>

    override fun execute(): Single<List<Team>> {
        single = SingleSubject.create<List<Team>>()
        return single
    }

    fun respondWithSuccess(teamList: List<Team>) {
        single.onSuccess(teamList)
    }

    fun respondWithError(error: Throwable) {
        single.onError(error)
    }

    fun isDisposed(): Boolean {
        return single.hasObservers().not()
    }
}

class GetAllTeamsUseCaseExceptionStub : GetAllTeamsUseCaseDummy() {

    override fun execute(): Single<List<Team>> {
        return Single.fromCallable { throw KotlinNullPointerException() }
    }
}