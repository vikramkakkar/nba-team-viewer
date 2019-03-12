package com.appeaser.nbateamviewer.domain.usecase

import com.appeaser.nbateamviewer.*
import com.appeaser.nbateamviewer.domain.data.TeamViewerRepositoryImpl
import com.appeaser.nbateamviewer.domain.entity.Team
import com.appeaser.nbateamviewer.domain.usecase.GetAllTeamsUseCaseImpl.Companion.RETRY_COUNT
import com.appeaser.nbateamviewer.external.network.HttpGateway
import okhttp3.mockwebserver.Dispatcher
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okhttp3.mockwebserver.RecordedRequest
import okhttp3.mockwebserver.SocketPolicy.NO_RESPONSE
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import retrofit2.HttpException
import java.net.SocketTimeoutException
import java.util.concurrent.TimeUnit

class GetAllTeamsUseCaseImplTest {

    @get:Rule
    val server = MockWebServer()

    @Test
    fun `success response`() {
        val body = SUCCESS_RESPONSE_BODY
        server.enqueue(createResponse(200, body))
        val useCase = createUseCase()

        val observer = useCase.execute().test()

        val expectedTeamList = fromJson<List<Team>>(body)
        observer.assertComplete()
        observer.assertNoErrors()
        observer.assertValue(expectedTeamList)
    }

    @Test
    fun `error - socket timeout`() {
        server.enqueue(MockResponse().apply {
            socketPolicy = NO_RESPONSE
        })
        val useCase = createUseCase()

        val observer = useCase.execute().test()

        observer.assertNotComplete()
        observer.assertError {
            it is GetAllTeamsUseCase.Exception.ConnectionIssueException
                    && it.cause is SocketTimeoutException
        }
    }

    @Test
    fun `error - malformed json`() {
        server.enqueue(createResponse(200, """{ invalid_json }"""))
        val useCase = createUseCase()

        val observer = useCase.execute().test()

        observer.assertNotComplete()
        observer.assertError {
            it is GetAllTeamsUseCase.Exception.GenericException
        }
    }

    @Test
    fun `error - unknown host`() {
        val useCase = createUseCase(baseUrl = "http://UnknownHost.Exception")

        val observer = useCase.execute().test()

        observer.assertNotComplete()
        observer.assertError {
            it is GetAllTeamsUseCase.Exception.ConnectionIssueException
        }
    }

    @Test
    fun `error - http status 4xx`() {
        val httpErrorCode = random400Error()
        server.enqueue(createResponse(httpErrorCode, ""))
        val useCase = createUseCase()

        val observer = useCase.execute().test()

        observer.assertNotComplete()
        observer.assertError {
            it is GetAllTeamsUseCase.Exception.HttpFailureException
                    && it.cause is HttpException
        }
    }

    @Test
    fun `error - http status 5xx`() {
        val httpErrorCode = random500Error()
        server.enqueue(createResponse(httpErrorCode, ""))
        val useCase = createUseCase()

        val observer = useCase.execute().test()

        observer.assertNotComplete()
        observer.assertError {
            it is GetAllTeamsUseCase.Exception.HttpFailureException
                    && it.cause is HttpException
        }
    }

    @Test
    fun `retry on http error 5xx`() {
        val httpErrorCode = random500Error()
        // will return the specified [MockResponse] for all incoming requests
        server.dispatcher = object : Dispatcher() {
            override fun dispatch(request: RecordedRequest?): MockResponse {
                return createResponse(httpErrorCode, "")
            }
        }
        val useCase = createUseCase(retryCount = RETRY_COUNT)

        useCase.execute().test()

        assertEquals(RETRY_COUNT + 1L /* original call */, server.requestCount.toLong())
    }

    @Test
    fun `retry on connection issue exception`() {
        // will return the specified [MockResponse] for all incoming requests
        server.dispatcher = object : Dispatcher() {
            override fun dispatch(request: RecordedRequest?): MockResponse {
                return MockResponse().apply { socketPolicy = NO_RESPONSE }
            }
        }
        val useCase = createUseCase(retryCount = RETRY_COUNT)

        useCase.execute().test()

        assertEquals(RETRY_COUNT + 1L /* original call */, server.requestCount.toLong())
    }

    @Test
    fun `don't retry on http error 4xx`() {
        val httpErrorCode = random400Error()
        // will return the specified [MockResponse] for all incoming requests
        server.dispatcher = object : Dispatcher() {
            override fun dispatch(request: RecordedRequest?): MockResponse {
                return createResponse(httpErrorCode, "")
            }
        }
        val useCase = createUseCase(retryCount = RETRY_COUNT)

        useCase.execute().test()

        assertEquals(1L /* original call & no retries */, server.requestCount.toLong())
    }

    @Test
    fun `success on retry`() {
        val httpErrorCode = random500Error()
        // failure
        server.enqueue(createResponse(httpErrorCode, ""))
        // success
        server.enqueue(createResponse(200, SUCCESS_RESPONSE_BODY))
        val useCase = createUseCase(retryCount = RETRY_COUNT)

        val observer = useCase.execute().test()

        observer.assertComplete()
        assertEquals(1L /* failure */ + 1L /* success */, server.requestCount.toLong())
    }

    private fun createUseCase(
        baseUrl: String = server.url("/").toString(),
        retryCount: Long = 0
    ): GetAllTeamsUseCaseImpl {
        val gateway = HttpGateway.build(
            baseUrl = baseUrl,
            timeout = 5L,
            timeoutUnit = TimeUnit.MILLISECONDS,
            cache = null
        )

        val repository = TeamViewerRepositoryImpl(gateway)

        return GetAllTeamsUseCaseImpl(repository, retryCount)
    }
}