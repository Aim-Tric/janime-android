package cn.codebro.j_anime_android.ui.slideshow

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.datastore.preferences.core.edit
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.lifecycle.lifecycleScope
import cn.codebro.j_anime_android.JAnimeApplication
import cn.codebro.j_anime_android.LOGIN_USER_PK
import cn.codebro.j_anime_android.R
import cn.codebro.j_anime_android.core.BaseFragment
import cn.codebro.j_anime_android.core.IView
import cn.codebro.j_anime_android.databinding.FragmentUserInfoBinding
import cn.codebro.j_anime_android.loginUserDataStore
import cn.codebro.j_anime_android.pojo.LoginUserVO
import cn.codebro.j_anime_android.presenter.UserInfoPresenter
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.coroutines.coroutineContext

interface UserInfoView : IView {
    fun logout()
}

class UserInfoFragment : BaseFragment(), UserInfoView {

    private var _binding: FragmentUserInfoBinding? = null
    private val userInfoPresenter: UserInfoPresenter = UserInfoPresenter(this)
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentUserInfoBinding.inflate(inflater, container, false)
        val root: View = binding.root

        context?.let {
            val loginUserLiveData =
                it.loginUserDataStore.data.asLiveData(lifecycleScope.coroutineContext)
            loginUserLiveData.observe(viewLifecycleOwner) { pf ->
                val loginUserPreferences = pf[LOGIN_USER_PK]
                if (loginUserPreferences.isNullOrBlank()) {
                    return@observe
                }
                val loginUserVO =
                    JAnimeApplication.gson.fromJson(loginUserPreferences, LoginUserVO::class.java)
                binding.usernameTextView.text = loginUserVO.nickname
            }

            lifecycleScope.launch(Dispatchers.IO) {
                it.loginUserDataStore.data.collect { pf ->
                    pf[LOGIN_USER_PK]?.let { loginUserJsonString ->
                        val loginUserVO = JAnimeApplication.gson.fromJson(
                            loginUserJsonString,
                            LoginUserVO::class.java
                        )
                        activity?.runOnUiThread {
                            binding.usernameTextView.text = loginUserVO.username
                            Glide.with(root)
                                .asDrawable()
                                .skipMemoryCache(true)
                                .diskCacheStrategy(DiskCacheStrategy.NONE)
                                .placeholder(R.drawable.ic_launcher_background)
                                .load(loginUserVO.avatar)
                                .centerCrop()
                                .into(binding.userAvatarImageView)
                        }
                    }
                }
            }
        }

        binding.logoutButton.setOnClickListener {
            userInfoPresenter.logout()
        }

        return root
    }

    override fun logout() {
        lifecycleScope.launch {
            context?.loginUserDataStore?.edit {
                it.clear()
            }

            notLogin()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}