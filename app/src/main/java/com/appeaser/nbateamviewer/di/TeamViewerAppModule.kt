package com.appeaser.nbateamviewer.di

import com.appeaser.nbateamviewer.domain.data.TeamViewerRepository
import com.appeaser.nbateamviewer.domain.data.TeamViewerRepositoryImpl
import com.appeaser.nbateamviewer.domain.usecase.GetAllTeamsUseCase
import com.appeaser.nbateamviewer.domain.usecase.GetAllTeamsUseCaseImpl
import com.appeaser.nbateamviewer.domain.usecase.GetTeamByIdUseCase
import com.appeaser.nbateamviewer.domain.usecase.GetTeamByIdUseCaseImpl
import com.appeaser.nbateamviewer.external.network.HttpGateway
import dagger.Module
import dagger.Provides
import okhttp3.Cache
import java.io.File

@Module
class TeamViewerAppModule {

    @ApplicationScope
    @Provides
    fun httpGateway(@CacheDir cacheDir: File): HttpGateway {
        val cache = Cache(cacheDir, HttpGateway.CACHE_SIZE)
        return HttpGateway.build(cache = cache)
    }

    @ApplicationScope
    @Provides
    fun teamViewerRepository(httpGateway: HttpGateway): TeamViewerRepository {
        return TeamViewerRepositoryImpl(httpGateway)
    }

    @ApplicationScope
    @Provides
    fun getAllTeamsUseCase(teamViewerRepository: TeamViewerRepository): GetAllTeamsUseCase {
        return GetAllTeamsUseCaseImpl(teamViewerRepository)
    }

    @ApplicationScope
    @Provides
    fun getTeamByIdUseCase(teamViewerRepository: TeamViewerRepository): GetTeamByIdUseCase {
        return GetTeamByIdUseCaseImpl(teamViewerRepository)
    }
}