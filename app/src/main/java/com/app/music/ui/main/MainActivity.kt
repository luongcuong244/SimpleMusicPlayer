package com.app.music.ui.main

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.findNavController
import com.app.music.R
import com.app.music.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var navController: NavController
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        navController = findNavController(R.id.nav_host_fragment)
        setupListeners()
    }

    private fun setupListeners() {
        binding.llTabHome.setOnClickListener {
            if (navController.currentDestination?.id != R.id.homeFragment) {
                selectTab(R.id.homeFragment)
                navController.navigate(R.id.homeFragment)
            }
        }
        binding.llTabFavourite.setOnClickListener {
            if (navController.currentDestination?.id != R.id.favouriteFragment) {
                selectTab(R.id.favouriteFragment)
                navController.navigate(R.id.favouriteFragment)
            }
        }
        binding.llTabLocal.setOnClickListener {
            if (navController.currentDestination?.id != R.id.musicLocalFragment) {
                selectTab(R.id.musicLocalFragment)
                navController.navigate(R.id.musicLocalFragment)
            }
        }
    }

    private fun selectTab(tabId: Int) {
        unSelectAllTabs()
        when (tabId) {
            R.id.homeFragment -> {
                binding.ivHome.setImageResource(R.drawable.ic_tab_home_active)
                binding.tvHome.setTextColor(resources.getColor(R.color.color_3C67FF))
            }
            R.id.favouriteFragment -> {
                binding.ivFavourite.setImageResource(R.drawable.ic_tab_favourite_active)
                binding.tvFavourite.setTextColor(resources.getColor(R.color.color_3C67FF))
            }
            R.id.musicLocalFragment -> {
                binding.ivLocal.setImageResource(R.drawable.ic_tab_local_active)
                binding.tvLocal.setTextColor(resources.getColor(R.color.color_3C67FF))
            }
        }
    }

    private fun unSelectAllTabs() {
        binding.ivHome.setImageResource(R.drawable.ic_tab_home_inactive)
        binding.tvHome.setTextColor(resources.getColor(R.color.color_787B82))
        binding.ivFavourite.setImageResource(R.drawable.ic_tab_favourite_inactive)
        binding.tvFavourite.setTextColor(resources.getColor(R.color.color_787B82))
        binding.ivLocal.setImageResource(R.drawable.ic_tab_local_inactive)
        binding.tvLocal.setTextColor(resources.getColor(R.color.color_787B82))
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment)
        val currentFragment = navHostFragment?.childFragmentManager?.fragments?.get(0)
        currentFragment?.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }
}