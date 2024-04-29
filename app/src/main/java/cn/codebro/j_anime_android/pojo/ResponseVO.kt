package cn.codebro.j_anime_android.pojo

import androidx.datastore.core.CorruptionException
import androidx.datastore.core.Serializer
import androidx.datastore.preferences.protobuf.InvalidProtocolBufferException
import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import java.io.InputStream
import java.io.OutputStream
import java.math.BigInteger


data class LoginUserVO(
    val id: Long,
    val loginStatus: Boolean,
    val username: String,
    val nickname: String,
    val avatar: String,
    val token: String
)

data class ApiPage<T>(val pageNo: Int, val pageSize: Int, val records: List<T>, val total: Long)

data class OpusHomeVO(
    val id: String,
    val nameOriginal: String,
    val nameCn: String,
    val coverUrl: String?,
    val detailInfoUrl: String,
    val userId: BigInteger,
    val hasResource: Int,
    val episodes: String?,
    val launchStart: String?,
    val deliveryWeek: String,
    val readingNum: Int,
    val readingTime: BigInteger,
    val readStatus: Int
)

data class OpusMediaVO(
    val aniSummary: String,
    val coverUrl: String,
    val deliveryWeek: String,
    val detailInfoUrl: String,
    val episodes: String,
    val hasResource: Int,
    val id: String,
    val isFollow: Int,
    val launchStart: String,
    val nameCn: String,
    val nameOriginal: String,
    val readingNum: Int,
    val readingTime: String,
    val readStatus: Int,
    val rssExcludeRes: String,
    val rssFileType: String,
    val rssLevelIndex: Int,
    val rssOnlyMark: String,
    val rssStatus: Int,
    val rssUrl: String,
    val userId: String,
    val userOpusId: String,
    val quarterList: Any?,
    val aniTags: List<AniTag>,
    val mediaList: List<MediaListType>
)

data class AniTag(val tagName: String, val id: String)
data class MediaListType(val episodes: String, val mediaType: String)
