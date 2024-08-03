package cn.codebro.j_anime_android.net

import cn.codebro.j_anime_android.pojo.ApiResponse
import cn.codebro.j_anime_android.pojo.CaptchaDTO
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface TestConnService {
    @POST("system/captcha")
    @Headers("Content-Type: application/json; charset=UTF-8")
    fun captcha(@Body codeType: CaptchaDTO): Call<ApiResponse<String>>
}