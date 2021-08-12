package com.example.texting.gaston.blogApp.domain.camera

import android.graphics.Bitmap
import com.example.texting.gaston.blogApp.data.remote.camera.CameraDataSource

class CameraRepoImpl(private val dataSource: CameraDataSource) : CameraRepo{
    override suspend fun upload(imageBitmap: Bitmap, description: String) {
        dataSource.uploadPhoto(imageBitmap, description)
    }

}