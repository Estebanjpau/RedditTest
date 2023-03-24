package com.example.reddittest.main_adapter

import android.view.View
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.reddittest.MainActivity
import com.example.reddittest.PostModel

open class PostModelViewHolder(view: View, private val mainInstance: MainActivity) : ViewHolder(view) {

    open var postVoteDir = ""

    open fun paint(postExample: PostModel) {

    }

}