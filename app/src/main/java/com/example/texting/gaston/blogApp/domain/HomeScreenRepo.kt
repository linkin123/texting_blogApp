package com.example.texting.gaston.blogApp.domain

import com.example.texting.gaston.blogApp.core.Result
import com.example.texting.gaston.blogApp.data.model.Post

interface HomeScreenRepo {
    suspend fun getLatestPost(): List<Post>

}