package com.example.texting.gaston.blogApp.ui.home

import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.example.texting.R
import com.example.texting.gaston.blogApp.core.Result
import com.example.texting.databinding.FragmentHomeScreenBinding
import com.example.texting.gaston.blogApp.core.hide
import com.example.texting.gaston.blogApp.core.show
import com.example.texting.gaston.blogApp.core.toast
import com.example.texting.gaston.blogApp.data.remote.home.HomeScreenDataSource
import com.example.texting.gaston.blogApp.domain.HomeScreenRepoImpl
import com.example.texting.gaston.blogApp.presentation.HomeScreenViewModel
import com.example.texting.gaston.blogApp.presentation.HomeScreenViewModelFactory
import com.example.texting.gaston.blogApp.ui.home.adapter.HomeScreenAdapter

class HomeScreenFragment : Fragment(R.layout.fragment_home_screen) {

    private lateinit var binding: FragmentHomeScreenBinding
    private val viewModel by viewModels<HomeScreenViewModel> {
        HomeScreenViewModelFactory(HomeScreenRepoImpl(HomeScreenDataSource()))
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentHomeScreenBinding.bind(view)
        viewModel.fetchLatestPosts().observe(viewLifecycleOwner, Observer { result ->
            when (result) {
                is Result.Loading -> {
                    binding.progressBar.show()
                }
                is Result.Success -> {


                    binding.progressBar.hide()

                    /*si no hay post*/
                    if (result.data.isEmpty()) {
                        with(binding){
                            emptyContainer.show()
                            rvHome.hide()
                        }
                        return@Observer
                    }

                    /*si hay post*/
                    binding.emptyContainer.hide()
                    with(binding.rvHome) {
                        show()
                        adapter = HomeScreenAdapter(result.data)
                    }
                }
                is Result.Failure -> {
                    binding.progressBar.hide()
                    toast(requireContext(), "Ocurrio un error ${result.exception}")
                }

            }
        })


    }


}