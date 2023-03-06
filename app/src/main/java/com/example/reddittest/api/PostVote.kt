package com.example.reddittest.api

import android.os.AsyncTask
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException

class PostVote(val id:String, val dir:String,val accesstoken:String) :
    AsyncTask<Void, Void, Boolean>() {

    override fun doInBackground(vararg params: Void?): Boolean? {
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
        try {
            val response = client.newCall(request).execute()
            return response.isSuccessful
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return false
    }

    override fun onPostExecute(result: Boolean?) {
        if (result == true) {
        }
    }
}
