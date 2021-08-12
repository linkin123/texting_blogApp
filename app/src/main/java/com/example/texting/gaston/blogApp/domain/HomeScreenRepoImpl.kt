package com.example.texting.gaston.blogApp.domain

import com.example.texting.gaston.blogApp.core.Result
import com.example.texting.gaston.blogApp.data.model.Post
import com.example.texting.gaston.blogApp.data.remote.home.HomeScreenDataSource

class HomeScreenRepoImpl(private val dataSource: HomeScreenDataSource) : HomeScreenRepo {
    override suspend fun getLatestPost(): List<Post> = dataSource.getLatestPosts()
}