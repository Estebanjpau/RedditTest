package com.example.reddittest.adapter

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.reddittest.PostModel
import com.example.reddittest.R

class PostModelViewHolder(view: View) : ViewHolder(view){

    val tvuserName = view.findViewById<TextView>(R.id.tvUserId)
    val tvalias = view.findViewById<TextView>(R.id.tvAlias)
    val tvtitle = view.findViewById<TextView>(R.id.tvTitle)
    val tvtimeAgo = view.findViewById<TextView>(R.id.tvTimeAgo)
    val ivUserPhoto = view.findViewById<ImageView>(R.id.ivUserPhoto)
    val ivPostImage = view.findViewById<ImageView>(R.id.ivPostImage)


    fun paint(postExample: PostModel){
        tvuserName.text = postExample.userId
        tvalias.text = postExample.alias
        tvtitle.text = postExample.title
        tvtimeAgo.text = postExample.timeAgo
    }

}