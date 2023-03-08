package com.example.reddittest.api

import okhttp3.*
import java.io.IOException

interface CheckAvailableUsername {

    fun getAvailableUsername(username:String, onComplete: (Boolean) -> Unit)  {
        val client = OkHttpClient()
        val url = "https://www.reddit.com/api/username_available.json?user=${username}"

        val request = Request.Builder()
            .url(url)
            .build()

        client.newCall(request).enqueue(
            object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    onComplete(false)
                }

                override fun onResponse(call: Call, response: Response) {
                    val jsonResponse = response.body?.string()
                    val isAvailable = jsonResponse?.toBoolean()
                    onComplete(isAvailable ?: false)
                }
            })
    }
}
