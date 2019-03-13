package com.appeaser.nbateamviewer.testdoubles

import com.appeaser.nbateamviewer.domain.data.TeamViewerRepository
import com.appeaser.nbateamviewer.domain.entity.Team
import io.reactivex.Single
import io.reactivex.subjects.SingleSubject

open class TeamViewerRepositoryDummy : TeamViewerRepository {
    override fun getAllTeams(): Single<List<Team>> {
        return Single.create<List<Team>> { }.doOnSubscribe {
            throw NotImplementedError("invocation on dummy instance")
        }
    }
}

class TeamViewerRepositoryFake : TeamViewerRepositoryDummy() {

    private lateinit var single: SingleSubject<List<Team>>

    override fun getAllTeams(): Single<List<Team>> {
        single = SingleSubject.create<List<Team>>()
        return single
    }

    fun respondWithTeamList(teamList: List<Team>) {
        single.onSuccess(teamList)
    }

    fun respondWithError(error: Throwable) {
        single.onError(error)
    }
}