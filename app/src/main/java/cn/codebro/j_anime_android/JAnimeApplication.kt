package cn.codebro.j_anime_android

import android.app.Application
import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.room.Room
import androidx.work.WorkManager
import cn.codebro.j_anime_android.core.event.EventBus
import cn.codebro.j_anime_android.data.AppDatabase
import cn.codebro.j_anime_android.net.ApiManager
import cn.codebro.j_anime_android.net.PersistentCookieStore
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.shuyu.gsyvideoplayer.player.PlayerFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import tv.danmaku.ijk.media.exo2.Exo2PlayerManager


val Context.loginUserDataStore: DataStore<Preferences> by preferencesDataStore(name = "loginUser")

class JAnimeApplication : Application() {
    init {
        instance = this
        eventBus = EventBus()
        gson = GsonBuilder()
            .setDateFormat("yyyy-MM-dd HH:mm:ss")
            .create()
    }

    override fun onCreate() {
        super.onCreate()
        initApplication()
    }

    private fun initApplication() {
        workManager = WorkManager.getInstance(this@JAnimeApplication)
        runBlocking(Dispatchers.IO) {
            launch {
                db = Room.databaseBuilder(
                    applicationContext,
                    AppDatabase::class.java, "janime-db"
                ).build()
                apiManager = ApiManager(gson, PersistentCookieStore())
            }
        }
        PlayerFactory.setPlayManager(Exo2PlayerManager::class.java)
    }


    companion object {
        lateinit var eventBus: EventBus
        lateinit var instance: JAnimeApplication
        var apiManager: ApiManager? = null
        lateinit var gson: Gson
        var db: AppDatabase? = null
        var workManager: WorkManager? = null
    }

}
