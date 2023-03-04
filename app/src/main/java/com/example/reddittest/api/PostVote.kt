package com.example.reddittest.api

import android.os.AsyncTask
import android.view.View
import com.example.reddittest.MainActivity
import com.example.reddittest.PostModel
import com.example.reddittest.adapter.PostModelViewHolder
import com.example.reddittest.databinding.ItemPostBinding
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException

class PostVote(view: View, val postExample: PostModel, val postHolderExample: PostModelViewHolder) :
    AsyncTask<Void, Void, Boolean>() {

    val mainInstance = MainActivity()
    val holderInstance = PostModelViewHolder(view, mainInstance)

    private val binding = ItemPostBinding.bind(view)

    override fun doInBackground(vararg params: Void?): Boolean? {
        val client = OkHttpClient()
        val url = "https://oauth.reddit.com/api/vote"
        val requestBody = FormBody.Builder()
            .add("id", postExample.postId)
            .add("dir", holderInstance.postVoteDir)
            .build()
        val request = Request.Builder()
            .url(url)
            .addHeader("Authorization", "Bearer ${mainInstance.accessToken}")
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
        super.onPostExecute(result)
        if (result == true) {
            if (postHolderExample.postVoteDir != "0") {
                if (postHolderExample.postVoteDir == "1") {

                }
            }
        }
    }
}