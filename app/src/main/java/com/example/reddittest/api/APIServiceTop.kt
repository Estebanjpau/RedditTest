package com.example.reddittest.api

import com.example.reddittest.SubredditListResponse
import com.example.reddittest.TopResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query


interface APIServiceTop {

    @GET("top")
    suspend fun getRedditTop(): Response<TopResponse>
}
interface APIServiceSearchSubreddit {
    @GET("subreddit")
    suspend fun getSubredditSearch(): Response<SubredditListResponse>
}