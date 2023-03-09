package com.example.reddittest.api

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.Credentials
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import java.io.IOException

class FetchAccessTokenTask(private val listener: AccessTokenListener) {

    private val client = OkHttpClient()

    fun execute(username: String?, password: String?, loginUser: String, loginPassword: String) {
        GlobalScope.launch(Dispatchers.IO) {
            try {
                val credentials = Credentials.basic(username ?: "", password ?: "")
                val requestBody = FormBody.Builder()
                    .add("grant_type", "password")
                    .add("username", loginUser)
                    .add("password", loginPassword)
                    .build()
                val request = Request.Builder()
                    .url("https://www.reddit.com/api/v1/access_token")
                    .header("Authorization", credentials)
                    .post(requestBody)
                    .build()

                val response = client.newCall(request).execute()
                val jsonResponse = JSONObject(response.body?.string() ?: "")
                val accessToken = jsonResponse.getString("access_token")

                withContext(Dispatchers.Main) {
                    listener.onAccessTokenFetched(accessToken)
                }
            } catch (e: IOException) {
                withContext(Dispatchers.Main) {
                    listener.onAccessTokenFetched(null)
                }
            }
        }
    }
}

interface AccessTokenListener {
    fun onAccessTokenFetched(getAccessToken: String?) {}
}