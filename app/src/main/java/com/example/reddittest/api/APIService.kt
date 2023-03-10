package com.example.reddittest.api

import com.example.reddittest.TopResponse
import retrofit2.Response
import retrofit2.http.GET


interface APIService {

    @GET("top")
    suspend fun getRedditTop(): Response<TopResponse>
}