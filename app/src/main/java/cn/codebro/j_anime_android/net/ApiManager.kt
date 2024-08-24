package cn.codebro.j_anime_android.net

import cn.codebro.j_anime_android.BuildConfig
import com.google.gson.Gson
import okhttp3.Cookie
import okhttp3.CookieJar
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

var OPUS_COVER_URL = "anime/opus/cover?resName="

fun toOpusMediaUrl(opusId: String, episode: String, mediaType: String = "mp4"): String {
    return "${BuildConfig.BASE_URL}anime/opus/media/${opusId}?resName=${episode}.${mediaType}"
}

class ApiManager(private val gson: Gson, val persistentCookieStore: PersistentCookieStore) {
    private var httpLoggingInterceptor = HttpLoggingInterceptor()
    private var isReady: Boolean = false;
    var okHttpClient: OkHttpClient
    private var retrofit: Retrofit? = null
    var baseUrl: String? = null

    init {
        httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
        val okHttpClientBuilder: OkHttpClient.Builder = OkHttpClient.Builder()
        okHttpClientBuilder.callTimeout(5, TimeUnit.MINUTES)
        okHttpClientBuilder.connectTimeout(5, TimeUnit.MINUTES)
        okHttpClientBuilder.writeTimeout(5, TimeUnit.MINUTES)
        okHttpClientBuilder.readTimeout(5, TimeUnit.MINUTES)
        okHttpClientBuilder.addInterceptor(httpLoggingInterceptor)
        okHttpClientBuilder.cookieJar(CookieManger(persistentCookieStore))
        okHttpClient = okHttpClientBuilder.build()
    }

    fun setup(url: String) {
        baseUrl = url
        retrofit = Retrofit.Builder()
            .baseUrl(url)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .client(okHttpClient)
            .build()
        isReady = true
    }

    private fun <T> createService(target: Class<T>): T {
        if (isReady) return retrofit!!.create(target)
        throw IllegalStateException("This 'ApiManager' did not ready to work.")
    }

    fun userService(): UserService {
        return createService(UserService::class.java)
    }

    fun opusService(): OpusService {
        return createService(OpusService::class.java)
    }

    class CookieManger(private val cookieStore: PersistentCookieStore) : CookieJar {
        override fun saveFromResponse(url: HttpUrl, cookies: List<Cookie>) {
            if (cookies.isEmpty()) {
                return;
            }

            for (item in cookies) {
                cookieStore.add(url, item)
            }
        }

        override fun loadForRequest(url: HttpUrl): List<Cookie> {
            return cookieStore[url]
        }
    }
}
