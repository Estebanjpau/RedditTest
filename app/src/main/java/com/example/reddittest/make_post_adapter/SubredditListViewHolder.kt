package com.example.reddittest.make_post_adapter

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.reddittest.R
import com.example.reddittest.SubredditChildrenList
import com.example.reddittest.databinding.ItemPostBinding
import com.example.reddittest.databinding.SubredditpostItemBinding

class SubredditListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    private val binding = SubredditpostItemBinding.bind(itemView)

    fun bind(subreddit: SubredditChildrenList) {
        binding.tvSubredditName.text = subreddit.subredditPrefixed

        if (subreddit.subredditImage != "") {
            Glide.with(binding.ivSubredditImage.context).load(subreddit.subredditImage)
                .into(binding.ivSubredditImage)
        }

        binding.tvSubredditMembers.text = subreddit.suscribers

        if (subreddit.CheckSuscription == "false"){
            binding.tvChecksuscribe.text = ""
            binding.tvSubredditDot.text = ""
        } else{
            binding.tvChecksuscribe.text = "Suscrito"
        }
    }
}
