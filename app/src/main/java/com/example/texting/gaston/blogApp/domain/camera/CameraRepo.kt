package com.example.texting.gaston.blogApp.domain.camera

import android.graphics.Bitmap

interface CameraRepo {
    suspend fun upload(imageBitmap: Bitmap, description : String)
}
