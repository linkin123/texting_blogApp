package com.example.texting.gaston.blogApp.ui.camera

import android.app.Activity.RESULT_OK
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.texting.R
import com.example.texting.databinding.FragmentCameraBinding
import com.example.texting.gaston.blogApp.core.Result
import com.example.texting.gaston.blogApp.data.remote.camera.CameraDataSource
import com.example.texting.gaston.blogApp.data.remote.home.HomeScreenDataSource
import com.example.texting.gaston.blogApp.domain.HomeScreenRepoImpl
import com.example.texting.gaston.blogApp.domain.camera.CameraRepoImpl
import com.example.texting.gaston.blogApp.presentation.HomeScreenViewModel
import com.example.texting.gaston.blogApp.presentation.HomeScreenViewModelFactory
import com.example.texting.gaston.blogApp.presentation.camera.CameraViewModel
import com.example.texting.gaston.blogApp.presentation.camera.CameraViewModelFactory


class CameraFragment : Fragment(R.layout.fragment_camera) {

    private val REQUEST_IMAGE_CAPTURE = 2
    private lateinit var binding: FragmentCameraBinding
    private var bitmap: Bitmap? = null

    private val viewModel by viewModels<CameraViewModel> {
        CameraViewModelFactory(CameraRepoImpl(CameraDataSource()))
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentCameraBinding.bind(view)
        onclicks()
    }

    private fun onclicks() {
        binding.btnUploadPhoto.setOnClickListener {
            bitmap?.let {
                viewModel.uploadPhoto(bitmap!!, binding.etxtDescription.text.toString().trim())
                    .observe(viewLifecycleOwner, { result ->
                        when (result) {
                            is Result.Loading -> {
                                Toast.makeText(requireContext(), "subiendo post", Toast.LENGTH_SHORT).show()
                            }
                            is Result.Success -> {
                                findNavController().navigate(R.id.action_cameraFragment_to_homeScreenFragment2)
                            }
                            is Result.Failure -> {
                                Toast.makeText(requireContext(), "Error : ${result.exception}", Toast.LENGTH_SHORT).show()
                            }
                        }
                    })
            }
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        try {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(requireContext(), "no se encontro ninguna camara", Toast.LENGTH_SHORT)
                .show()
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            val imageBitmap = data?.extras?.get("data") as Bitmap
            binding.postImage.setImageBitmap(imageBitmap)
            bitmap = imageBitmap
        }
    }
}