package cn.codebro.j_anime_android

import android.os.Bundle
import android.view.Menu
import com.google.android.material.navigation.NavigationView
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.asLiveData
import androidx.lifecycle.lifecycleScope
import cn.codebro.j_anime_android.databinding.ActivityMainBinding
import cn.codebro.j_anime_android.databinding.NavHeaderMainBinding
import cn.codebro.j_anime_android.core.BaseView
import cn.codebro.j_anime_android.pojo.LoginUserVO

class MainActivity : BaseView() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding
    private lateinit var navHeaderMainBinding: NavHeaderMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        navHeaderMainBinding =
            NavHeaderMainBinding.inflate(layoutInflater, binding.navView, true)
        setContentView(binding.root)
        setSupportActionBar(binding.appBarMain.toolbar)

        setupDrawerLayout()
        setupNav()

        val loginUserLiveData =
            loginUserDataStore.data.asLiveData(lifecycleScope.coroutineContext)
        loginUserLiveData.observe(this) {
            val loginUserPreferences = it[LOGIN_USER_PK]
            if (loginUserPreferences.isNullOrBlank()) {
                return@observe
            }
            val loginUserVO =
                JAnimeApplication.gson.fromJson(loginUserPreferences, LoginUserVO::class.java)
            navHeaderMainBinding.nickNameTextView.text = loginUserVO.nickname
        }
    }

    private fun setupDrawerLayout() {
        val drawerLayout: DrawerLayout = binding.drawerLayout
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow
            ), drawerLayout
        )
    }

    private fun setupNav() {
        val navView: NavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_content_main)

        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

}