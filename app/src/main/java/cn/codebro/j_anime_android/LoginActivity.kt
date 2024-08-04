package cn.codebro.j_anime_android

import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Base64
import androidx.lifecycle.lifecycleScope
import cn.codebro.j_anime_android.core.BaseView
import cn.codebro.j_anime_android.core.IView
import cn.codebro.j_anime_android.databinding.ActivityLoginBinding
import cn.codebro.j_anime_android.pojo.CaptchaVO
import cn.codebro.j_anime_android.pojo.LoginDTO
import cn.codebro.j_anime_android.pojo.LoginUserVO
import cn.codebro.j_anime_android.presenter.UserPresenter
import cn.hutool.crypto.SecureUtil
import cn.hutool.crypto.SmUtil
import cn.hutool.crypto.asymmetric.KeyType
import cn.hutool.crypto.asymmetric.SM2
import kotlinx.coroutines.launch


interface LoginView : IView {
    fun setCaptcha(captchaResp: CaptchaVO)
    fun loginSuccess()
    fun loginFail(message: String)
}

class LoginActivity : BaseView(), LoginView {
    private lateinit var presenter: UserPresenter
    private lateinit var binding: ActivityLoginBinding
    private var captchaId: String? = null
    private var publicKey: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        presenter = UserPresenter(this)

        checkLoginStatus()
        bindEvent()

        presenter.loadCaptcha()
        // 测试用代码
        binding.usernameEditText.setText("")
        binding.passwordEditText.setText("")
    }

    private fun checkLoginStatus() {
        lifecycleScope.launch {
            loginUserDataStore.data.collect {
                val loginUserPreferences = it[LOGIN_USER_PK]
                if (loginUserPreferences.isNullOrBlank()) {
                    return@collect
                }
                val loginUserVO = JAnimeApplication.gson.fromJson(
                    loginUserPreferences,
                    LoginUserVO::class.java
                )
                if (loginUserVO.loginStatus) {
                    presenter.refreshUserInfo()
                    loginSuccess()
                }
            }
        }
    }

    private fun bindEvent() {
        binding.captchaImageView.setOnClickListener {
            presenter.loadCaptcha()
        }
        binding.loginButton.setOnClickListener {
            val username = binding.usernameEditText.text.trim()
            val password = binding.passwordEditText.text.trim()
            val captcha = binding.captchaEditText.text.trim()
            if (username.isEmpty()) {

                return@setOnClickListener
            }
            if (password.isEmpty()) {

                return@setOnClickListener
            }
            if (captcha.isEmpty()) {

                return@setOnClickListener
            }
            if (captchaId.isNullOrBlank() || publicKey.isNullOrBlank())
                showToast("请联系作者排查异常....")
            else {
                val pair = SecureUtil.generateKeyPair(publicKey!!)
                val encodedPublicKey = pair.public.encoded
                val sm2: SM2 = SmUtil.sm2(null, encodedPublicKey)
                val encryptPassword = sm2.encrypt(password.toString().toByteArray()).toString()

                presenter.login(
                    LoginDTO(
                        captchaId!!,
                        username.toString(),
                        encryptPassword,
                        publicKey!!,
                        captcha.toString()
                    )
                )
            }

        }
    }

    override fun setCaptcha(captchaResp: CaptchaVO) {
        captchaId = captchaResp.captchaId
        publicKey = captchaResp.publicKey
        val decodedBytes: ByteArray =
            Base64.decode(captchaResp.imgBase64, Base64.DEFAULT)
        val bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
        binding.captchaImageView.setImageBitmap(bitmap)
    }

    override fun loginSuccess() {
        runOnUiThread {
            val intent = Intent(this@LoginActivity, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
        }
    }

    override fun loginFail(message: String) {
        showToast(message)
        presenter.loadCaptcha()
    }
}