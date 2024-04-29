package cn.codebro.j_anime_android.net

import cn.codebro.j_anime_android.pojo.ApiPage
import cn.codebro.j_anime_android.pojo.ApiResponse
import cn.codebro.j_anime_android.pojo.OpusHomeVO
import cn.codebro.j_anime_android.pojo.OpusHomeDTO
import cn.codebro.j_anime_android.pojo.OpusMediaVO
import cn.codebro.j_anime_android.pojo.OpusUpdateProgressDTO
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Path

interface OpusService {

    @POST("anime/opus/listByUser")
    @Headers("Content-Type: application/json; charset=UTF-8")
    fun listByPage(@Body opusHomeDTO: OpusHomeDTO): Call<ApiResponse<ApiPage<OpusHomeVO>?>>

    @GET("anime/opus/getOpusMedia/{opusId}")
    @Headers("Content-Type: application/json; charset=UTF-8")
    fun getOpusMedia(@Path("opusId") opusId: String): Call<ApiResponse<OpusMediaVO?>>

    @POST("anime/userOpus/updateProgress")
    @Headers("Content-Type: application/json; charset=UTF-8")
    fun updateProgress(@Body opusUpdateProgressDTO: OpusUpdateProgressDTO): Call<ApiResponse<Boolean?>>

}