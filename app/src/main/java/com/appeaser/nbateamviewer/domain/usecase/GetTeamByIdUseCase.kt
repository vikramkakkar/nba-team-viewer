package com.appeaser.nbateamviewer.domain.usecase

import com.appeaser.nbateamviewer.domain.data.TeamViewerRepository
import com.appeaser.nbateamviewer.domain.entity.Team
import io.reactivex.Single

/**
 * Returns [Team] with the given ID.
 */
interface GetTeamByIdUseCase {

    fun execute(teamId: Int): Single<Team>

    sealed class Exception(cause: Throwable? = null) : kotlin.Exception(cause) {
        class NotFoundException : Exception()
    }
}

class GetTeamByIdUseCaseImpl(private val repository: TeamViewerRepository) : GetTeamByIdUseCase {
    override fun execute(teamId: Int): Single<Team> {
        return repository.getAllTeams()
            .map { teamList ->
                val team = teamList.find { team ->
                    teamId == team.id
                }

                return@map team ?: throw GetTeamByIdUseCase.Exception.NotFoundException()
            }
    }
}