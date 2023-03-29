package com.example.reddittest.make_post_adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.example.reddittest.R
import com.example.reddittest.data_model.SubredditChildrenList

class SubredditListAdapter(private var subredditList: MutableSet<SubredditChildrenList>, val listener: OnSubredditClickListener):
    ListAdapter<SubredditChildrenList, SubredditListViewHolder>(SubredditDiffCallback()) {

    var filteredList = ArrayList<SubredditChildrenList>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SubredditListViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.subredditpost_item, parent, false)
        return SubredditListViewHolder(itemView, listener )
    }

    override fun onBindViewHolder(holder: SubredditListViewHolder, position: Int) {
        val subreddit = filteredList[position]
        holder.bind(subreddit)
    }

    override fun getItemCount(): Int {
        return filteredList.size
    }

    private class SubredditDiffCallback : DiffUtil.ItemCallback<SubredditChildrenList>() {

        override fun areItemsTheSame(oldItem: SubredditChildrenList, newItem: SubredditChildrenList): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: SubredditChildrenList, newItem: SubredditChildrenList): Boolean {
            return oldItem == newItem
        }
    }

    fun filterList(query: String) {
        filteredList.clear()
        if (query.isNotEmpty()) {
            for (subreddit in subredditList) {
                if (subreddit.subredditPrefixed.contains(query, true)) {
                    filteredList.add(subreddit)
                } else if (query.isEmpty()){
                    filteredList.clear()
                }
            }
        }
        notifyDataSetChanged()
    }
}

interface OnSubredditClickListener {
    fun onSubredditClick(subreddit: SubredditChildrenList)
}