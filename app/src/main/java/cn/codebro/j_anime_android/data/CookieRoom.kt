package cn.codebro.j_anime_android.data

import androidx.room.ColumnInfo
import androidx.room.Dao
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.PrimaryKey
import androidx.room.Query

@Entity("cookies")
data class CookieEntity(
    @PrimaryKey val id: Int?,
    @ColumnInfo(name = "host") val host: String,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "value") val value: String,
    @ColumnInfo(name = "domain") val domain: String,
    @ColumnInfo(name = "path") val path: String,
    @ColumnInfo(name = "expired_at") val expiredAt: Long,
    @ColumnInfo(name = "secure") val secure: Boolean,
    @ColumnInfo(name = "http_only") val httpOnly: Boolean,
    @ColumnInfo(name = "host_only") val hostOnly: Boolean
)

@Dao
interface CookieDao {
    @Query("select * FROM cookies WHERE host = :host")
    fun getListByHost(host: String): List<CookieEntity>
    @Query("select * FROM cookies")
    fun getAll(): List<CookieEntity>
    @Insert
    fun insertAll(vararg users: CookieEntity)
}

