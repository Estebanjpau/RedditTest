package com.example.reddittest.api

import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import java.io.IOException

interface GetUserData {

    suspend fun getLoggedUsername(tokenAccess: String): String {
        val client = OkHttpClient()
        val url = "https://oauth.reddit.com/api/v1/me"
        val bearerToken = "bearer " + tokenAccess

        val request = Request.Builder()
            .url(url)
            .header("Authorization", bearerToken)
            .build()

        val response = client.newCall(request).execute()
        response.use {
            if (response.isSuccessful) {
                val jsonResponse = JSONObject(response.body?.string() ?: "")
                val username = jsonResponse.getJSONObject("subreddit")
                    .getString("display_name_prefixed")
                return username
            }else {
                throw IOException("Error obteniendo el nombre de usuario")
            }
        }
    }
}