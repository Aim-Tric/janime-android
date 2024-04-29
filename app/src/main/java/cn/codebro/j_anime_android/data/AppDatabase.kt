package cn.codebro.j_anime_android.data

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [CookieEntity::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun cookieDao(): CookieDao
}