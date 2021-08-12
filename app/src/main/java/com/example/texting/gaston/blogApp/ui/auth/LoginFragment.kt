package com.example.texting.gaston.blogApp.ui.auth

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.texting.R
import com.example.texting.databinding.FragmentLoginBinding
import com.example.texting.gaston.blogApp.core.Result
import com.example.texting.gaston.blogApp.data.remote.auth.AuthDataSource
import com.example.texting.gaston.blogApp.domain.auth.AuthRepoImpl
import com.example.texting.gaston.blogApp.presentation.auth.AuthViewModel
import com.example.texting.gaston.blogApp.presentation.auth.AuthViewModelFactory
import com.google.firebase.auth.FirebaseAuth

class LoginFragment : Fragment(R.layout.fragment_login) {

    private lateinit var binding: FragmentLoginBinding

    private val firebaseAuth by lazy { FirebaseAuth.getInstance() }

    private val viewmodel by viewModels<AuthViewModel> {
        AuthViewModelFactory(AuthRepoImpl(AuthDataSource()))
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentLoginBinding.bind(view)
        isUserLoggedIn()
        doLogin()
        goToSignUpPage()
    }

    private fun isUserLoggedIn() {
        firebaseAuth.currentUser?.let {user->
            if(user.displayName.isNullOrEmpty()){
                findNavController().navigate(R.id.action_loginFragment_to_setUpProfileFragment)
            }else{
                findNavController().navigate(R.id.action_loginFragment_to_homeScreenFragment2)
            }
        }
    }

    private fun doLogin() {
        binding.btnSignin.setOnClickListener {
            val email = binding.editTextEmail.text.toString().trim()
            val password = binding.editTextPassword.text.toString().trim()
            if (validateCredentials(email, password)) {
                signIn(email, password)
            }
        }
    }

    private fun goToSignUpPage() {
        binding.txtSignup.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
        }
    }


    private fun signIn(email: String, password: String) {
        viewmodel.signIn(email, password).observe(viewLifecycleOwner, { result ->
            when (result) {
                is Result.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                    binding.btnSignin.isEnabled = false
                }
                is Result.Success -> {
                    binding.progressBar.visibility = View.GONE

                    if(result.data?.displayName.isNullOrEmpty()){
                        findNavController().navigate(R.id.action_loginFragment_to_setUpProfileFragment)
                    }else{
                        Toast.makeText(requireContext(), "Welcome ${result.data?.displayName}", Toast.LENGTH_SHORT).show()
                        findNavController().navigate(R.id.action_loginFragment_to_homeScreenFragment2)
                    }
                }
                is Result.Failure -> {
                    binding.progressBar.visibility = View.GONE
                    binding.btnSignin.isEnabled = true
                    Toast.makeText(
                        requireContext(), "Error ${result.exception}",
                        Toast.LENGTH_SHORT
                    ).show()

                }
            }
        })
    }

    private fun validateCredentials(email: String, password: String): Boolean {
        if (email.isEmpty()) {
            binding.editTextEmail.error = "E-mail es vacío"
            return false
        }
        if (password.isEmpty()) {
            binding.editTextPassword.error = "password es vacío"
            return false
        }

        return true
    }


}