package com.example.reddittest.adapter

import android.view.View
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.example.reddittest.PostModel
import com.example.reddittest.databinding.ItemPostBinding

class PostModelViewHolder(view: View) : ViewHolder(view) {

    val binding = ItemPostBinding.bind(view)


    fun paint(postExample: PostModel) {
        binding.tvUserId.text = postExample.userId
        binding.tvAlias.text = postExample.alias
        binding.tvTitle.text = postExample.title
        binding.tvTimeAgo.text = postExample.timeAgo
        Glide.with(binding.ivUserPhoto.context).load(postExample.userPhoto).into(binding.ivUserPhoto)
        Glide.with(binding.ivPostImage.context).load(postExample.postPhoto).into(binding.ivPostImage)
    }

}