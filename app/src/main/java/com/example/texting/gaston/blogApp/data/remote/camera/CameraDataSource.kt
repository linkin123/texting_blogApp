package com.example.texting.gaston.blogApp.data.remote.camera

import android.graphics.Bitmap
import com.example.texting.gaston.blogApp.data.model.Post
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await
import java.io.ByteArrayOutputStream
import java.util.*

class CameraDataSource {
    suspend fun uploadPhoto(imageBitmap: Bitmap, description: String) {
        val user = FirebaseAuth.getInstance().currentUser
        val randomeName = UUID.randomUUID().toString()
        val imageRef =
            FirebaseStorage.getInstance().reference.child("${user?.uid}/posts/$randomeName")
        val baos = ByteArrayOutputStream()
        imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val downloadUrl =
            imageRef.putBytes(baos.toByteArray()).await().storage.downloadUrl.await().toString()

        user?.let {
            it.displayName?.let { displayName ->
                FirebaseFirestore.getInstance().collection("posts")
                    .add(
                        Post(
                            profile_name = displayName,
                            profile_picture = it.photoUrl.toString(),
                            post_image = downloadUrl,
                            post_description = description,
                            uid = user.uid
                        )
                    )
            }
        }

    }
}