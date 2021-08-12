package com.example.texting.gaston.blogApp.ui.auth

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.texting.R
import com.example.texting.databinding.FragmentRegisterBinding
import com.example.texting.gaston.blogApp.core.Result
import com.example.texting.gaston.blogApp.data.remote.auth.AuthDataSource
import com.example.texting.gaston.blogApp.domain.auth.AuthRepoImpl
import com.example.texting.gaston.blogApp.presentation.auth.AuthViewModel
import com.example.texting.gaston.blogApp.presentation.auth.AuthViewModelFactory
import com.google.android.material.textfield.TextInputEditText


class RegisterFragment : Fragment(R.layout.fragment_register) {

    private lateinit var binding: FragmentRegisterBinding
    private val viewmodel by viewModels<AuthViewModel> {
        AuthViewModelFactory(AuthRepoImpl(AuthDataSource()))
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentRegisterBinding.bind(view)
        signUp()
    }

    private fun signUp() {
        with(binding) {

            btnSignup.setOnClickListener {

                val username = editTextUsername.text.toString().trim()
                val password = editTextPassword.text.toString().trim()
                val confirmPassword = editTextConfirmPassword.text.toString().trim()
                val email = editTextEmail.text.toString().trim()


                if (validateUserData(
                        password,
                        confirmPassword,
                        username,
                        email
                    )
                ) return@setOnClickListener

                createUser(email, password, username)
            }

        }


    }

    private fun createUser(email: String, password: String, username: String) {
        viewmodel.signUp(email, password, username).observe(viewLifecycleOwner, { result ->
            when (result) {
                is Result.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                    binding.btnSignup.isEnabled = false

                }
                is Result.Success -> {
                    binding.progressBar.visibility = View.GONE
                    findNavController().navigate(R.id.action_registerFragment_to_setUpProfileFragment)
                }
                is Result.Failure -> {
                    binding.progressBar.visibility = View.GONE
                    binding.btnSignup.isEnabled = true
                    Toast.makeText(requireContext(), "Erro : ${result.exception}", Toast.LENGTH_SHORT).show()
                }
            }
        })
    }

    private fun FragmentRegisterBinding.validateUserData(
        password: String,
        confirmPassword: String,
        username: String,
        email: String
    ): Boolean {
        if (password != confirmPassword) {
            editTextConfirmPassword.error = "password does not match"
            editTextPassword.error = "password does not match"
            return true
        }

        if (username.isEmpty()) {
            binding.editTextUsername.error = "error user empty"
            return true
        }
        if (email.isEmpty()) {
            binding.editTextEmail.error = "error email empty"
            return true
        }

        if (password.isEmpty()) {
            binding.editTextPassword.error = "error pass empty"
            return true
        }

        if (confirmPassword.isEmpty()) {
            binding.editTextConfirmPassword.error = "error confirm pass empty"
            return true
        }
        return false
    }
}

private fun TextInputEditText.mIsEmpty(msg: String) {
    if (this.text.toString().trim().isEmpty()) {
        this.error = msg
        return
    }
}