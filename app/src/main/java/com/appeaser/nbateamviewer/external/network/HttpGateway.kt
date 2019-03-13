package com.appeaser.nbateamviewer.external.network

import android.util.Log
import com.appeaser.nbateamviewer.domain.entity.Team
import io.reactivex.Single
import okhttp3.Cache
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Headers
import java.util.concurrent.TimeUnit

interface HttpGateway {

    @Headers(CACHE_CONTROL_HEADER)
    @GET(FILE_URI)
    fun getInputData(): Single<List<Team>>

    companion object {

        private const val TAG = "[HttpGateway]"

        // endpoint
        private const val BASE_URL = "https://raw.githubusercontent.com/"
        private const val FILE_URI = "scoremedia/nba-team-viewer/master/input.json"

        // 5 MB
        const val CACHE_SIZE = 5L * 1024L * 1024L
        private const val CACHE_MAX_AGE = 24L * 60L * 60L

        // we'll accept responses that are at most one day past their expiry
        private const val CACHE_MAX_STALE = 24L * 60L * 60L
        private const val CACHE_CONTROL_HEADER = "Cache-Control: max-age=$CACHE_MAX_AGE, max-stale=$CACHE_MAX_STALE"

        fun build(
            baseUrl: String = BASE_URL,
            timeout: Long = 15L,
            timeoutUnit: TimeUnit = TimeUnit.SECONDS,
            cache: Cache?
        ): HttpGateway {
            val httpClient = OkHttpClient.Builder()
                // network caching
                .cache(cache)
                .connectTimeout(timeout, timeoutUnit)
                .readTimeout(timeout, timeoutUnit)
                .writeTimeout(timeout, timeoutUnit)
                .addLoggingInterceptor()
                .cache(cache)
                .retryOnConnectionFailure(false)
                .build()

            return Retrofit.Builder()
                .client(httpClient)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(baseUrl)
                .build()
                .create(HttpGateway::class.java)
        }

        /**
         * Logs request/response info to logcat. We'll only log the http body for debug builds.
         */
        private fun OkHttpClient.Builder.addLoggingInterceptor(): OkHttpClient.Builder {
            val loggingInterceptor = HttpLoggingInterceptor {
                Log.i(TAG, it)
            }

            loggingInterceptor.level = if (com.appeaser.nbateamviewer.BuildConfig.DEBUG) {
                HttpLoggingInterceptor.Level.BODY
            } else {
                HttpLoggingInterceptor.Level.HEADERS
            }

            addInterceptor(loggingInterceptor)
            return this
        }
    }
}