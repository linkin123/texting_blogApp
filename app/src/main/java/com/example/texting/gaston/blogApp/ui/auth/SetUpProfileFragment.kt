package com.example.texting.gaston.blogApp.ui.auth

import android.app.Activity.RESULT_OK
import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.texting.R
import com.example.texting.databinding.FragmentSetUpProfileBinding
import com.example.texting.gaston.blogApp.core.Result
import com.example.texting.gaston.blogApp.data.remote.auth.AuthDataSource
import com.example.texting.gaston.blogApp.domain.auth.AuthRepoImpl
import com.example.texting.gaston.blogApp.presentation.auth.AuthViewModel
import com.example.texting.gaston.blogApp.presentation.auth.AuthViewModelFactory

class SetUpProfileFragment : Fragment(R.layout.fragment_set_up_profile) {

    private val REQUEST_IMAGE_CAPTURE = 1
    private var bitmap: Bitmap? = null

    private lateinit var binding: FragmentSetUpProfileBinding
    private val viewmodel by viewModels<AuthViewModel> {
        AuthViewModelFactory(AuthRepoImpl(AuthDataSource()))
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentSetUpProfileBinding.bind(view)
        onclicks()
    }

    private fun onclicks() {
        binding.profileImage.setOnClickListener {
            val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            try {
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
            } catch (e: ActivityNotFoundException) {
                Toast.makeText(
                    requireContext(),
                    "no se encontro ninguna camara",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        binding.btnCreateProfile.setOnClickListener {
            val username = binding.etxtUsername.text.toString().trim()
            val alertDialog= AlertDialog.Builder(requireContext()).setTitle("Uploading photo ...").create()
            bitmap?.let {
                if (username.isNotEmpty()) {
                    viewmodel.updateUserProfile(imageBitmap = bitmap!!, username = username)
                        .observe(viewLifecycleOwner, { result ->
                            when (result) {
                                is Result.Loading->{
                                    alertDialog.show()
                                }
                                is Result.Success->{
                                    alertDialog.dismiss()
                                    findNavController().navigate(R.id.action_setUpProfileFragment_to_homeScreenFragment2)
                                }
                                is Result.Failure->{
                                    alertDialog.dismiss()
                                }

                            }
                        })
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            val imageBitmap = data?.extras?.get("data") as Bitmap
            binding.profileImage.setImageBitmap(imageBitmap)
            bitmap = imageBitmap
        }
    }
}