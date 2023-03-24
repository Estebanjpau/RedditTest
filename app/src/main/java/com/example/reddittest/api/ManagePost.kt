package com.example.reddittest.api

import android.util.Log
import com.example.reddittest.SubredditChildrenList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.*
import org.json.JSONObject
import java.io.IOException

interface ManagePost {

    @Throws(IOException::class, RedditException::class)
    suspend fun makePost(access_token: String, title: String, text: String, urlPost: String, kind: String
    ): Response {
        return withContext(Dispatchers.IO) {
            val url = "https://oauth.reddit.com/api/submit"
            val accessToken = access_token

            val requestBody = MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("kind", kind)
                .addFormDataPart("title", title)
                .addFormDataPart("text", text)
                .addFormDataPart("sr", "")
                .build()

            val request = Request.Builder()
                .url(url)
                .header("Authorization", "Bearer $accessToken")
                .post(requestBody)
                .build()

            val client = OkHttpClient()
            Log.d("ManagePost", "Solicitud: $request")
            val response = client.newCall(request).execute()

            print(response)

            if (!response.isSuccessful) {
                Log.d("ManagePost", "Error en la solicitud: ${response.message}")
                throw RedditException(response.message)
            }

            return@withContext response
        }
    }

    fun searchSubreddit(access_token : String) : String? {
        val client = OkHttpClient()
        val accessToken = access_token
        val url = "https://oauth.reddit.com/subreddits"

        val request = Request.Builder()
            .url(url)
            .header("Authorization", "Bearer $accessToken")
            .build()

        val response = client.newCall(request).execute()
        return response.body?.string()
    }

    fun parseSubreddits(jsonString: String): List<SubredditChildrenList> {
        val jsonObject = JSONObject(jsonString)
        val jsonArray = jsonObject.getJSONArray("subreddits")

        val subreddits = mutableListOf<SubredditChildrenList>()
        for (i in 0 until jsonArray.length()) {
            val subJson = jsonArray.getJSONObject(i)
            val subreddit = SubredditChildrenList(
                "",
                "",
                "",
                subJson.getString("name"),
                subJson.getString("url")
            )
            subreddits.add(subreddit)
        }

        return subreddits
    }


    class RedditException(message: String) : Exception(message)
}
