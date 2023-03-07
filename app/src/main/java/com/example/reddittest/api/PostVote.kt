package com.example.reddittest.api

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException

suspend fun postVote(id:String, dir:String, accesstoken:String): Boolean {
    val client = OkHttpClient()
    val url = "https://oauth.reddit.com/api/vote"
    val requestBody = FormBody.Builder()
        .add("id", id)
        .add("dir", dir)
        .build()
    val request = Request.Builder()
        .url(url)
        .addHeader("Authorization", "Bearer ${accesstoken}")
        .post(requestBody)
        .build()
    return withContext(Dispatchers.IO) {
        try {
            val response = client.newCall(request).execute()
            response.isSuccessful
        } catch (e: IOException) {
            e.printStackTrace()
            false
        }
    }
}

