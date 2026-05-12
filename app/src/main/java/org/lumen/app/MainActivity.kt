package org.lumen.app

import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.core.view.isVisible
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import com.google.android.material.navigation.NavigationView
import org.lumen.app.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() , NavigationView.OnNavigationItemSelectedListener {

    private lateinit var binding: ActivityMainBinding
    private var noToolbar = intArrayOf(R.id.splashFragment, R.id.loginFragment)

    override fun onCreate(savedInstanceState: Bundle?) {


        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        WindowCompat.setDecorFitsSystemWindows(window, false)
        WindowInsetsControllerCompat(window, window.decorView).hide(WindowInsetsCompat.Type.statusBars())

        initNav()
        initToolbar()


    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        Toast.makeText(this, item.title, Toast.LENGTH_SHORT).show()

        when(item.itemId){
            R.id.nav_profile -> {
                findNavController(R.id.action_homeFragment_to_loginFragment)
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

            binding.appBarLayout.isVisible = destination.id !in noToolbar

            when (destination.id) {
                R.id.homeFragment -> {

                    binding.toolbar.navigationIcon = null
                }
                else -> {

                    binding.toolbar.setNavigationIcon(R.drawable.ic_chevron_left)
                }
            }



            binding.toolbar.title = ""
        }
    }
}