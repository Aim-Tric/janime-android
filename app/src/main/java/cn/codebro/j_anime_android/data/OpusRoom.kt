package cn.codebro.j_anime_android.data

import androidx.paging.PagingSource
import androidx.room.ColumnInfo
import androidx.room.Dao
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import java.math.BigInteger

@Entity("home_opus")
data class OpusEntity(
    @PrimaryKey val id: String,
    @ColumnInfo(name = "name_original") val nameOriginal: String,
    @ColumnInfo(name = "name_cn") val nameCn: String,
    @ColumnInfo(name = "cover_url") val coverUrl: String?,
    @ColumnInfo(name = "detail_info_url") val detailInfoUrl: String,
    @ColumnInfo(name = "user_id") val userId: Long,
    @ColumnInfo(name = "has_resource") val hasResource: Int,
    @ColumnInfo(name = "episodes") val episodes: String?,
    @ColumnInfo(name = "launch_start") val launchStart: String?,
    @ColumnInfo(name = "delivery_week") val deliveryWeek: String,
    @ColumnInfo(name = "reading_num") val readingNum: Int,
    @ColumnInfo(name = "reading_time") val readingTime: Long,
    @ColumnInfo(name = "read_status") val readStatus: Int
)

@Dao
interface OpusDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(users: List<OpusEntity>)

    @Query("SELECT * FROM home_opus")
    fun pagingSource(): PagingSource<Int, OpusEntity>

    @Query("DELETE FROM home_opus")
    suspend fun clearAll()
}