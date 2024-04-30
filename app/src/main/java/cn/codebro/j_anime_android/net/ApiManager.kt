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


//const val BASE_URL = "http://192.168.10.100:8088/api/"
var OPUS_COVER_URL = "${BuildConfig.BASE_URL}anime/opus/cover?resName="

fun toOpusMediaUrl(opusId: String, episode: String, mediaType: String = "mp4"): String {
    return "${BuildConfig.BASE_URL}anime/opus/media/${opusId}?resName=${episode}.${mediaType}"
}

class ApiManager(gson: Gson, val persistentCookieStore: PersistentCookieStore) {
    private var httpLoggingInterceptor = HttpLoggingInterceptor()
    private var retrofit: Retrofit
    var okHttpClient: OkHttpClient

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
        retrofit = Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .client(okHttpClient)
            .build()
    }

    fun userService(): UserService {
        return retrofit.create(UserService::class.java)
    }

    fun opusService(): OpusService {
        return retrofit.create(OpusService::class.java)
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
