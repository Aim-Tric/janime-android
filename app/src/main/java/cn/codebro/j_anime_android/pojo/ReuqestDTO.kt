package cn.codebro.j_anime_android.pojo

import java.util.Date

class ApiResponse<T> {
    var code: Int? = null
    var message: String? = null
    var time: Date? = null
    var data: T? = null

    constructor()
    constructor(code: Int?, message: String?, time: Date?, data: T?) {
        this.code = code
        this.message = message
        this.time = time
        this.data = data
    }
}

data class LoginDTO(
    val captchaId: String,
    val username: String,
    val password: String,
    val captcha: String,
    val publicKey: String,
    val rememberMe: Boolean = true
)

data class OpusHomeDTO(
    val pageNo: Int = 1, val pageSize: Int = 40, val searchKey: String = "",
    val hasResource: Int = 1,
    val months: List<Int>? = listOf(), val states: List<Int>? = listOf(),
    val status: List<Int>? = listOf(), val years: List<Int>? = listOf(),
)

data class OpusUpdateProgressDTO(val id: String, val readingNum: Int, val readingTime: Int)
