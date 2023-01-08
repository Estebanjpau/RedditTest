package com.example.reddittest.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.reddittest.PostModel
import com.example.reddittest.PostProvider.Companion.providerList
import com.example.reddittest.R

class PostModelAdapter(private val postModel:List<PostModel>) : RecyclerView.Adapter<PostModelViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostModelViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)

        return PostModelViewHolder(layoutInflater.inflate(R.layout.item_post,parent, false))
    }

    override fun onBindViewHolder(holder: PostModelViewHolder, position: Int) {
        val item = providerList[position]
        holder.paint(item)
    }

    override fun getItemCount(): Int = postModel.size
}