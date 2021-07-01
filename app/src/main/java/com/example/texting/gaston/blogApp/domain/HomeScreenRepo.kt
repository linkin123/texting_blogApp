package com.example.texting.gaston.blogApp.domain

import com.example.texting.gaston.blogApp.core.Resource
import com.example.texting.gaston.blogApp.data.model.Post

interface HomeScreenRepo {
    suspend fun getLatestPost(): Resource<List<Post>>

}