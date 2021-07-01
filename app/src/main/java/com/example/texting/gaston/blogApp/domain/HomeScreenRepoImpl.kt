package com.example.texting.gaston.blogApp.domain

import com.example.texting.gaston.blogApp.core.Resource
import com.example.texting.gaston.blogApp.data.model.Post
import com.example.texting.gaston.blogApp.data.remote.HomeScreenDataSource

class HomeScreenRepoImpl(private val dataSource: HomeScreenDataSource) : HomeScreenRepo {
    override suspend fun getLatestPost(): Resource<List<Post>> = dataSource.getLatestPosts()
}