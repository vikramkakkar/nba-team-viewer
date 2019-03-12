package com.appeaser.nbateamviewer.domain.data

import com.appeaser.nbateamviewer.domain.entity.Team
import com.appeaser.nbateamviewer.external.network.HttpGateway
import io.reactivex.Single

/**
 * Provides access to team data.
 */
interface TeamViewerRepository {
    fun getAllTeams(): Single<List<Team>>
}

class TeamViewerRepositoryImpl(private val gateway: HttpGateway) : TeamViewerRepository {

    // 'cache' the team list on success
    private var teamList: List<Team>? = null

    override fun getAllTeams(): Single<List<Team>> {
        return teamList?.let {
            Single.just(it)
        } ?: gateway.getInputData().doOnSuccess {
            teamList = it
        }
    }
}