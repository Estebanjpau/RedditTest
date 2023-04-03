package com.example.reddittest.api

import com.example.reddittest.data_model.SubredditListResponse
import com.example.reddittest.data_model.TopResponse
import retrofit2.Response
import retrofit2.http.GET


interface APIServiceTop {

    @GET("top")
    suspend fun getRedditTop(): Response<TopResponse>
}
interface APIServiceSearchSubreddit {
    @GET("subreddit")
    suspend fun getSubredditSearch(): Response<SubredditListResponse>
}