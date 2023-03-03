package com.example.reddittest.api

import android.os.AsyncTask
import okhttp3.Credentials
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request

class FetchAccessTokenTask(private val listener: AccessTokenListener) : AsyncTask<String, Void, String>() {

    override fun doInBackground(vararg params: String?): String {
        val username = params[0]
        val password = params[1]

        val client = OkHttpClient()

        val credentials = Credentials.basic(username!!, password!!)
        val requestBody = FormBody.Builder()
            .add("grant_type", "password")
            .add("username", "estebanjpau")
            .add("password", "wd1qi5fl")
            .build()
        val request = Request.Builder()
            .url("https://www.reddit.com/api/v1/access_token")
            .header("Authorization", credentials)
            .post(requestBody)
            .build()

        val response = client.newCall(request).execute()
        return response.body?.string() ?: ""
    }

    override fun onPostExecute(result: String?) {
        super.onPostExecute(result)
        listener.onAccessTokenFetched(result)
    }
}

interface AccessTokenListener {
    fun onAccessTokenFetched(getAccessToken: String?){}
}
