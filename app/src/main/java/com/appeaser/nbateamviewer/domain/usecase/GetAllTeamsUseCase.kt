package com.appeaser.nbateamviewer.domain.usecase

import com.appeaser.nbateamviewer.domain.data.TeamViewerRepository
import com.appeaser.nbateamviewer.domain.entity.Team
import io.reactivex.Single
import retrofit2.HttpException
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

interface GetAllTeamsUseCase {

    fun execute(): Single<List<Team>>

    sealed class Exception(cause: Throwable? = null) : kotlin.Exception(cause) {
        class ConnectionIssueException(errorCause: Throwable?) : Exception(errorCause)
        class HttpFailureException(errorCause: Throwable?, val canRetry: Boolean) : Exception(errorCause)
        class GenericException(errorCause: Throwable?) : Exception(errorCause)
    }
}

class GetAllTeamsUseCaseImpl(
    private val repository: TeamViewerRepository,
    private val retryCount: Long = RETRY_COUNT
) : GetAllTeamsUseCase {

    override fun execute(): Single<List<Team>> {
        return repository.getAllTeams()
            .onErrorResumeNext {
                return@onErrorResumeNext handleError(it).toSingleError()
            }.retry(retryCount, retryIf())
    }

    private fun handleError(error: Throwable?): GetAllTeamsUseCase.Exception {
        return when (error) {
            is HttpException -> {
                val canRetry = error.code() in 500..599
                GetAllTeamsUseCase.Exception.HttpFailureException(error, canRetry)
            }
            is SocketTimeoutException,
            is UnknownHostException,
            is ConnectException -> GetAllTeamsUseCase.Exception.ConnectionIssueException(error)
            else -> GetAllTeamsUseCase.Exception.GenericException(error)
        }
    }

    private fun retryIf(): (Throwable) -> Boolean = {
        when (it) {
            is GetAllTeamsUseCase.Exception.ConnectionIssueException -> true
            is GetAllTeamsUseCase.Exception.HttpFailureException -> it.canRetry
            else -> false
        }
    }

    private fun GetAllTeamsUseCase.Exception.toSingleError(): Single<List<Team>> {
        return Single.error(this)
    }

    companion object {
        const val RETRY_COUNT = 3L
    }
}