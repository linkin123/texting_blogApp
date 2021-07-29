package com.example.texting.gaston.blogApp.data.remote.home

import com.example.texting.gaston.blogApp.core.Resource
import com.example.texting.gaston.blogApp.data.model.Post
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class HomeScreenDataSource {
    suspend fun getLatestPosts(): Resource<List<Post>>{
        val postList = mutableListOf<Post>()
        val querySsnapshot = FirebaseFirestore.getInstance().collection("posts").get().await()
        for(post in querySsnapshot.documents){
            post.toObject(Post::class.java)?.let { fbPost->
                postList.add(fbPost)
            }
        }
        return Resource.Success(postList)
    }
}