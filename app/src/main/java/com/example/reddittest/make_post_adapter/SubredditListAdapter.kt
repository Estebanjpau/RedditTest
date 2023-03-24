package com.example.reddittest.make_post_adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.example.reddittest.R
import com.example.reddittest.SubredditChildrenList

class SubredditListAdapter(private var subredditList: List<SubredditChildrenList>) :
    ListAdapter<SubredditChildrenList, SubredditListViewHolder>(SubredditDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SubredditListViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.subredditpost_item, parent, false)
        return SubredditListViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: SubredditListViewHolder, position: Int) {
        val subreddit = subredditList[position]
        holder.bind(subreddit)
    }

    override fun getItemCount(): Int {
        return subredditList.size
    }

    private class SubredditDiffCallback : DiffUtil.ItemCallback<SubredditChildrenList>() {

        override fun areItemsTheSame(oldItem: SubredditChildrenList, newItem: SubredditChildrenList): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: SubredditChildrenList, newItem: SubredditChildrenList): Boolean {
            return oldItem == newItem
        }
    }
}

private class SubredditDiffCallback : DiffUtil.ItemCallback<String>() {

    override fun areItemsTheSame(oldItem: String, newItem: String): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: String, newItem: String): Boolean {
        return oldItem == newItem
    }
}






