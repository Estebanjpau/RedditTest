package com.example.reddittest.api

import android.graphics.BitmapFactory
import android.graphics.Bitmap
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException

interface ManagePost {

    @Throws(IOException::class, RedditException::class)
    suspend fun makePost(access_token: String, title: String, text: String?, urlPost: String?, image: Bitmap?, video:File?, kind: String, subreddit:String
    ): Response {
        return withContext(Dispatchers.IO) {
            val url = "https://oauth.reddit.com/api/submit"
            val accessToken = access_token

            val requestBody = MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("kind", kind)
                .addFormDataPart("title", title)
                .addFormDataPart("sr", subreddit)

            when(kind){
                "self" -> {
                    requestBody.addFormDataPart("text", text ?: "")
                }

                "link" -> {
                    requestBody.addFormDataPart("text", text ?: "")
                    requestBody.addFormDataPart("url", url)
                }

                "image", "video" -> {
                    val  mediaType = when(kind){
                        "image" -> "image/*"
                        "video" -> "video/*"
                        else ->throw IllegalArgumentException("Invalid kind value: $kind")
                    }

                    requestBody.addFormDataPart("media_type", mediaType)
                    requestBody.addFormDataPart("richtext_json", """{"document": [{"c": "${title}", "t": "t"}], "output_mode": "markdown"}""")

                    if (kind == "image") {
                        val outputStream = ByteArrayOutputStream()
                        image?.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
                        requestBody.addFormDataPart("image", "image.jpg",
                            outputStream.toByteArray().asRequestBody(mediaType))
                    } else if (kind == "video") {
                        requestBody.addFormDataPart("video_upload", video?.name ?: "")
                        if (video != null) {
                            requestBody.addFormDataPart("file", video.name ?: "", video.asRequestBody(mediaType.toMediaTypeOrNull()))
                        }
                    }
                }
                else -> throw IllegalArgumentException("Invalid kind value: $kind")
            }

            val request = Request.Builder()
                .url(url)
                .header("Authorization", "Bearer $accessToken")
                .post(requestBody.build())
                .build()

            val client = OkHttpClient()
            Log.d("ManagePost", "Solicitud: $request")
            val response = client.newCall(request).execute()

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

    class RedditException(message: String) : Exception(message)

    fun getSubredditRules (subreddit : String,) : String? {
        val client = OkHttpClient()
        val url = "https://www.reddit.com/${subreddit}/about/rules.json"

        val request = Request.Builder()
            .url(url)
            .build()

        val response = client.newCall(request).execute()

        return response.body?.string()
    }
}
