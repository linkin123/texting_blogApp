package com.example.texting.gaston.blogApp.data.remote.home

import com.example.texting.gaston.blogApp.core.Result
import com.example.texting.gaston.blogApp.data.model.Post
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QueryDocumentSnapshot
import kotlinx.coroutines.tasks.await

class HomeScreenDataSource {
    suspend fun getLatestPosts(): List<Post> {
        val postList = mutableListOf<Post>()
        val querySsnapshot = FirebaseFirestore.getInstance().collection("posts").orderBy("created_at", Query.Direction.DESCENDING).get().await()
        for(post in querySsnapshot.documents){
            post.toObject(Post::class.java)?.let { fbPost->
                fbPost.apply {
                    created_at = post.getTimestamp("created_at", DocumentSnapshot.ServerTimestampBehavior.ESTIMATE)?.toDate()
                }
                postList.add(fbPost)
            }
        }
        return postList
    }
}