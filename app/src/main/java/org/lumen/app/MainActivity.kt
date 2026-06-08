package org.lumen.app

import org.lumen.app.data.local.TokenManager
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.core.view.isVisible
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import com.bumptech.glide.Glide
import com.google.android.material.navigation.NavigationView
import org.lumen.app.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() , NavigationView.OnNavigationItemSelectedListener {

    private lateinit var binding: ActivityMainBinding
    private var noToolbar = intArrayOf(R.id.splashFragment, R.id.loginFragment)

    private lateinit var tokenManager : TokenManager

    override fun onCreate(savedInstanceState: Bundle?) {


        super.onCreate(savedInstanceState)

        tokenManager = TokenManager(this)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        WindowCompat.setDecorFitsSystemWindows(window, false)
        WindowInsetsControllerCompat(window, window.decorView).hide(WindowInsetsCompat.Type.statusBars())

        initNav()
        initToolbar()


    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.nav_logout -> {
                tokenManager.clear()
                findNavController(R.id.nav_host_fragment).navigate(R.id.loginFragment)
            }
            R.id.nav_profile -> {
                findNavController(R.id.nav_host_fragment).navigate(R.id.profileFragment)
            }
        }

        binding.drawerLayout.closeDrawer(GravityCompat.END)


        return true
    }

    private fun initToolbar() {
        val toolbar = binding.toolbar
        setSupportActionBar(toolbar)

        toolbar.setNavigationOnClickListener {
            this.onBackPressedDispatcher.onBackPressed()
        }

        binding.avatar.setOnClickListener {
            binding.drawerLayout.openDrawer(GravityCompat.END)
        }

        binding.navigationDrawer.setNavigationItemSelectedListener(this)
    }

    private fun initNav() {
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController

        navController.addOnDestinationChangedListener { _, destination, _ ->

            val toolbar = binding.toolbar
            val appBar = binding.appBarLayout
            val cardNav = binding.cardNav
            val avatar = binding.avatar


            toolbar.setNavigationIcon(R.drawable.ic_chevron_left)
            appBar.setBackgroundResource(R.color.md_theme_background)
            appBar.isVisible = true
            avatar.isVisible = true
            cardNav.isVisible = false

            Glide.with(this)
                .load(tokenManager.getProfileImage())
                .into(avatar)



            when (destination.id) {
                R.id.splashFragment -> {
                    appBar.isVisible = false
                }
                R.id.homeFragment -> {
                   toolbar.navigationIcon = null
                    cardNav.isVisible = true

                }
                R.id.loginFragment -> {
                    appBar.isVisible = false
                }
                R.id.registerFragment -> {
                    appBar.isVisible = false
                }
                R.id.sessionFragment -> {
                    appBar.setBackgroundResource(R.color.session_theme_background)
                }
                R.id.profileFragment -> {
                    avatar.isVisible = false

                }


            }



            binding.toolbar.title = ""
        }
    }
}