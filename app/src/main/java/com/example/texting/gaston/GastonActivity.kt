package com.example.texting.gaston

import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import com.example.texting.R
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.io.ByteArrayOutputStream
import java.util.*

class GastonActivity : AppCompatActivity() {

    val REQUEST_IMAGE_CAPTURE =1340
    private var imageView : ImageView? = null
    private var btn : Button? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gaston)
        imageView = findViewById(R.id.iv_imagen)
        btn = findViewById(R.id.btn_take_picture)

        onclicks()
        firebaseSetUpData()

    }

    private fun onclicks() {
        btn?.setOnClickListener {
            dispatchTakePictureIntent()
        }
    }

    private fun dispatchTakePictureIntent(){
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        try{
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
        }catch (e : ActivityNotFoundException){
            Toast.makeText(this, "no se encontro app para la foto", Toast.LENGTH_SHORT).show()
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode== REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK){
            val imageBitmap = data?.extras?.get("data") as Bitmap
            imageView?.setImageBitmap(imageBitmap)
            uploadPicture(imageBitmap)
        }
    }

    private fun firebaseSetUpData() {
        val db = FirebaseFirestore.getInstance()
        db.collection("ciudades").document("LA").get().addOnSuccessListener { document->
            document?.let {
                Log.d("firebase", "DocumentSnapShot data : ${document.data}")
            }
        }.addOnFailureListener{
            it.localizedMessage
        }

        db.collection("ciudades")
            .document("NY")
            .set(Ciudades(30000, "Red"))
            .addOnSuccessListener {

            }.addOnFailureListener {

            }
    }

    private fun uploadPicture(bitmap : Bitmap){
        val storageRef = FirebaseStorage.getInstance().reference
        val imageRef = storageRef.child("${UUID.randomUUID()}.jpg")
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos)
        val data = baos.toByteArray()
        val uploadTask = imageRef.putBytes(data)

        uploadTask.continueWithTask { task ->
            if(!task.isSuccessful){
                task.exception?.let { exception->
                    throw  exception
                }
            }
            imageRef.downloadUrl
        }.addOnCompleteListener { task ->
            if(task.isSuccessful){
                val downloadUrl = task.result.toString()
                FirebaseFirestore.getInstance()
                    .collection("ciudades")
                    .document("LA")
                    .update(mapOf("imageUrl" to downloadUrl))
                Log.d("Storage", "uploadPictureURL: $downloadUrl")
            }
        }

    }


}

data class Ciudades(val population : Int = 0, val color : String = "")