package com.appeaser.nbateamviewer.domain.usecase

import com.appeaser.nbateamviewer.SUCCESS_RESPONSE_BODY
import com.appeaser.nbateamviewer.domain.entity.Team
import com.appeaser.nbateamviewer.fromJson
import com.appeaser.nbateamviewer.testdoubles.TeamViewerRepositoryFake
import org.junit.Test
import kotlin.random.Random

class GetTeamByIdUseCaseImplTest {

    @Test
    fun `get team by ID success`() {
        // setup
        val successTeamList = fromJson<List<Team>>(SUCCESS_RESPONSE_BODY)
        val teamToGet = successTeamList[Random.nextInt(successTeamList.size)]
        val repository = TeamViewerRepositoryFake()
        val useCase = GetTeamByIdUseCaseImpl(repository)

        val observer = useCase.execute(teamToGet.id).test()
        repository.respondWithTeamList(successTeamList)

        // verify that we got the correct team
        observer.assertValue(teamToGet)
        observer.assertComplete()
    }

    @Test
    fun `get team by ID - not found`() {
        // setup
        val successTeamList = listOf(Team(1, "abc", 1, 2, listOf()))
        val repository = TeamViewerRepositoryFake()
        val useCase = GetTeamByIdUseCaseImpl(repository)

        val observer = useCase.execute(2).test()
        repository.respondWithTeamList(successTeamList)

        // verify that we got the correct team
        observer.assertNoValues()
        observer.assertError {
            it is GetTeamByIdUseCase.Exception.NotFoundException
        }
    }

    @Test
    fun `get team by ID - not found - empty list`() {
        // setup
        val successTeamList = listOf<Team>()
        val repository = TeamViewerRepositoryFake()
        val useCase = GetTeamByIdUseCaseImpl(repository)

        val observer = useCase.execute(1).test()
        repository.respondWithTeamList(successTeamList)

        // verify that we got the correct team
        observer.assertNoValues()
        observer.assertError {
            it is GetTeamByIdUseCase.Exception.NotFoundException
        }
    }

    @Test
    fun `get team by ID - exception`() {
        // setup
        val repository = TeamViewerRepositoryFake()
        val useCase = GetTeamByIdUseCaseImpl(repository)

        val observer = useCase.execute(1).test()
        repository.respondWithError(KotlinNullPointerException())

        // verify that we got the correct team
        observer.assertNoValues()
        observer.assertError {
            it is KotlinNullPointerException
        }
    }
}