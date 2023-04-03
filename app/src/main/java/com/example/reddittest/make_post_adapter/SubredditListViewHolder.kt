package com.example.reddittest.make_post_adapter

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.reddittest.*
import com.example.reddittest.data_model.SubredditChildrenList
import com.example.reddittest.databinding.SubredditpostItemBinding

class SubredditListViewHolder(itemView: View, private val listener: OnSubredditClickListener) : RecyclerView.ViewHolder(itemView){

    private val binding = SubredditpostItemBinding.bind(itemView)

    fun bind(subreddit: SubredditChildrenList) {
        binding.tvSubredditName.text = subreddit.subredditPrefixed

        Glide.with(binding.ivSubredditImage.context).load(subreddit.subredditImage).placeholder(R.drawable.reddit_svgrepo_com)
            .circleCrop().into(binding.ivSubredditImage)

        val resumeSubscribers = subreddit.suscribers.let {
            PostUtils.resumeCounterNumber(it.toInt())
        }

        binding.tvSubredditMembers.text = "$resumeSubscribers miembros"

        if (subreddit.CheckSuscription == "false") {
            binding.tvChecksuscribe.text = ""
            binding.tvSubredditDot.text = ""
        } else {
            binding.tvChecksuscribe.text = "Suscrito"
        }

        binding.clContentSubredditPostItem.setOnClickListener {
            listener.onSubredditClick(subreddit)
        }
    }
}
