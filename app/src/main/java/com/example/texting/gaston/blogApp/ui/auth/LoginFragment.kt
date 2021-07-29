package com.example.texting.gaston.blogApp.ui.auth

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.texting.R
import com.example.texting.databinding.FragmentLoginBinding
import com.example.texting.gaston.blogApp.core.Resource
import com.example.texting.gaston.blogApp.data.remote.auth.LoginDataSource
import com.example.texting.gaston.blogApp.domain.auth.LoginRepoImpl
import com.example.texting.gaston.blogApp.presentation.auth.LoginScreenViewModel
import com.example.texting.gaston.blogApp.presentation.auth.LoginScreenViewModelFactory
import com.google.firebase.auth.FirebaseAuth

class LoginFragment : Fragment(R.layout.fragment_login) {

    private lateinit var binding : FragmentLoginBinding

    private val firebaseAuth by lazy{ FirebaseAuth.getInstance() }

    private val viewmodel by viewModels<LoginScreenViewModel>{
        LoginScreenViewModelFactory(LoginRepoImpl(LoginDataSource()))
    }



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentLoginBinding.bind(view)
        isUserLoggedIn()
        doLogin()
    }

    private fun isUserLoggedIn() {
        firebaseAuth.currentUser?.let {
            findNavController().navigate(R.id.action_loginFragment_to_homeScreenFragment2)
        }
    }

    private fun doLogin(){
        binding.btnSignin.setOnClickListener {
            val email = binding.editTextEmail.text.toString().trim()
            val password = binding.editTextPassword.text.toString().trim()
            if(validateCredentials(email, password)){
                signIn(email, password)
            }
        }
    }

    private fun signIn(email: String, password: String) {
        viewmodel.signIn(email, password).observe(viewLifecycleOwner, {result ->
            when(result){
                is Resource.Loading-> {
                    binding.progressBar.visibility = View.VISIBLE
                    binding.btnSignin.isEnabled = false
                }
                is Resource.Success ->{
                    binding.progressBar.visibility = View.GONE
                    findNavController().navigate(R.id.action_loginFragment_to_homeScreenFragment2)
                }
                is Resource.Failure -> {
                    binding.progressBar.visibility = View.GONE
                    binding.btnSignin.isEnabled = true
                    Toast.makeText(requireContext(), "Error ${result.exception}",
                    Toast.LENGTH_SHORT).show()

                }
            }
        })
    }

    private fun validateCredentials(email : String , password : String): Boolean {
        if(email.isEmpty()){
            binding.editTextEmail.error = "E-mail es vacío"
            return false
        }
        if(password.isEmpty()){
            binding.editTextPassword.error = "password es vacío"
            return false
        }

        return true
    }



}