package com.appeaser.nbateamviewer.data

import androidx.test.core.app.ApplicationProvider
import com.appeaser.nbateamviewer.SUCCESS_RESPONSE_BODY
import com.appeaser.nbateamviewer.TeamViewerApp
import com.appeaser.nbateamviewer.createResponse
import com.appeaser.nbateamviewer.domain.data.TeamViewerRepositoryImpl
import com.appeaser.nbateamviewer.external.network.HttpGateway
import okhttp3.Cache
import okhttp3.mockwebserver.MockWebServer
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import java.util.concurrent.TimeUnit

@RunWith(RobolectricTestRunner::class)
class TeamViewerRepositoryImplTest {

    @get:Rule
    val server = MockWebServer()

    @Test
    fun `fetched team list should be cached by repository`() {
        val body = SUCCESS_RESPONSE_BODY
        server.enqueue(createResponse(200, body))
        val repository = createRepository()

        // call twice
        repository.getAllTeams().test()
        repository.getAllTeams().test()

        assertEquals(1, server.requestCount)
    }

    @Test
    fun `return network cached response if possible`() {
        val body = SUCCESS_RESPONSE_BODY
        server.enqueue(createResponse(200, body))
        val cache = Cache(ApplicationProvider.getApplicationContext<TeamViewerApp>().cacheDir, HttpGateway.CACHE_SIZE)
        val repository1 = createRepository(cache)
        val repository2 = createRepository(cache)

        // first call should go to the server
        repository1.getAllTeams().test().awaitDone(1, TimeUnit.SECONDS)

        // second call should be a cache hit
        repository2.getAllTeams().test().awaitDone(1, TimeUnit.SECONDS)

        assertEquals(1, server.requestCount)
        // response from cache
        assertEquals(1, cache.hitCount())
        // network request
        assertEquals(1, cache.networkCount())
        // total requests that came to [Cache]
        assertEquals(2, cache.requestCount())
    }

    private fun createRepository(cache: Cache? = null): TeamViewerRepositoryImpl {
        val gateway = HttpGateway.build(
            baseUrl = server.url("/").toString(),
            timeout = 5L,
            timeoutUnit = TimeUnit.MILLISECONDS,
            cache = cache
        )

        return TeamViewerRepositoryImpl(gateway)
    }
}