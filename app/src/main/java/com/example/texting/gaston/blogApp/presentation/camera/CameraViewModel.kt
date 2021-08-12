package com.example.texting.gaston.blogApp.presentation.camera

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.liveData
import com.example.texting.gaston.blogApp.core.Result
import com.example.texting.gaston.blogApp.domain.auth.AuthRepo
import com.example.texting.gaston.blogApp.domain.camera.CameraRepo
import com.example.texting.gaston.blogApp.presentation.auth.AuthViewModel
import kotlinx.coroutines.Dispatchers
import java.lang.Exception

class CameraViewModel(private val repo : CameraRepo): ViewModel() {
    fun uploadPhoto(imageBitmap : Bitmap, description : String) = liveData(Dispatchers.IO){
        emit(Result.Loading())
        try{
            emit(Result.Success(repo.upload(imageBitmap, description)))
        }catch (e: Exception){
            emit(Result.Failure(e))
        }
    }
}

class CameraViewModelFactory(private val repo : CameraRepo): ViewModelProvider.Factory{
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return modelClass.getConstructor(CameraRepo::class.java).newInstance(repo)
    }

}