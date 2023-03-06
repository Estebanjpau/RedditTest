package com.example.reddittest.api

import android.os.Handler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import java.io.IOException

class GetVoteDir(
    val subreddit: String,
    val postId: String,
    val authtoken: String,
    val username: String
) {

    private val handler: Handler = Handler()

    fun makeCall(): String {
        val url = "https://oauth.reddit.com/r/${subreddit}/comments/${postId}/?limit=1"
        val bearerToken = "bearer $authtoken"

        val request = Request.Builder()
            .url(url)
            .header("Authorization", bearerToken)
            .header("User-Agent", username)
            .build()

        val client = OkHttpClient()


        val response = client.newCall(request).execute()
        response.use {
            if (!response.isSuccessful) throw IOException("Unexpected code $response")

            val jsonResponse = JSONObject(response.body?.string() ?: "")
            val postArray = jsonResponse.getJSONArray("data").getJSONArray(0)
            val postObject = postArray.getJSONObject(0)

            val voteDir = postObject.getString("likes")

            return voteDir.toString()
        }
    }

    fun GetPostDirInBackground() {
        CoroutineScope(Dispatchers.IO).launch {
            var result = ""

            result = makeCall()
            if (result == "true" || result == "false" || result == "null") {
                handler.post(Runnable {
                    result
                })
            }
        }
    }
}