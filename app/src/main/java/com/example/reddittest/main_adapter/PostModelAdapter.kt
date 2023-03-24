package com.example.reddittest.main_adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.reddittest.MainActivity
import com.example.reddittest.PostModel
import com.example.reddittest.R

class PostModelAdapter(private val providerList: List<PostModel>,private val mainInstance:MainActivity) :
    RecyclerView.Adapter<PostModelViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostModelViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val item = providerList[viewType]

        return if(item.isVideo == true){

               val view = layoutInflater.inflate(R.layout.item_post_video, parent, false)
               PostModelVideoViewHolder(view, mainInstance)
           } else {
                val view = layoutInflater.inflate(R.layout.item_post, parent, false)
                PostModelImageViewHolder(view, mainInstance)
            }
            //    PostModelImageViewHolder(layoutInflater.inflate(R.layout.item_post, parent, false),mainInstance)
    }

    override fun onBindViewHolder(holder: PostModelViewHolder, position: Int) {
        val item = providerList[position]
        holder.paint(item)
    }

    override fun getItemCount(): Int = providerList.size

    override fun getItemViewType(position: Int): Int {
        return position
    }
}
