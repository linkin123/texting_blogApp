package com.example.texting.gaston.blogApp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.texting.R
import com.example.texting.databinding.ActivityMain2Binding
import com.example.texting.gaston.blogApp.core.hide
import com.example.texting.gaston.blogApp.core.show

class MainActivity : AppCompatActivity() {

    private lateinit var binding : ActivityMain2Binding
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMain2Binding.inflate(layoutInflater)
        setContentView(binding.root)
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment_container) as NavHostFragment
        navController = navHostFragment.navController
        binding.bottomNavigationView.setupWithNavController(navController)
        observeDestinationsChange()
    }

    private fun observeDestinationsChange() {
        navController.addOnDestinationChangedListener { controller, destination, arguments ->
            when(destination.id){
                R.id.loginFragment, R.id.registerFragment, R.id.setUpProfileFragment->{
                    binding.bottomNavigationView.hide()
                }
                else->{
                    binding.bottomNavigationView.show()
                }
            }
        }
    }
}


