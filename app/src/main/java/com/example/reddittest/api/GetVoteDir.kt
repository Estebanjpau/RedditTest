package com.example.reddittest.api

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONArray

class GetVoteDir(val subreddit: String, val postId: String, val authtoken: String, val username: String, var voteDir: String = ""
) {

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
            if (response.isSuccessful) {


                val jsonResponse = JSONArray(response.body?.string() ?: "")
                val dataObject =
                    jsonResponse.getJSONObject(0).getJSONObject("data").getJSONArray("children")
                        .getJSONObject(0).getJSONObject("data")

                val childrenArray = dataObject.getString("likes")
                voteDir = childrenArray
            }
            return voteDir
        }
    }

    fun GetPostDirInBackground() : Deferred<String> {
        return CoroutineScope(Dispatchers.IO).async {
            makeCall()
        }
    }
}