package com.example.reddittest

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.reddittest.api.ManagePost
import com.example.reddittest.databinding.FragmentSearchSubredditBinding
import com.example.reddittest.make_post_adapter.SubredditListAdapter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject

class SearchSubredditFragment : Fragment(), ManagePost {

    private var subredditListItems = mutableListOf<SubredditChildrenList>()

    private lateinit var adapter: SubredditListAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var binding: FragmentSearchSubredditBinding
    private lateinit var mainInstance: MainActivity

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSearchSubredditBinding.inflate(inflater, container, false)

        mainInstance = activity as MainActivity

        val actionBar = (activity as AppCompatActivity).supportActionBar
        actionBar?.hide()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)



        recyclerView = binding.rvSubredditList
        recyclerView.layoutManager = LinearLayoutManager(activity)
        adapter = SubredditListAdapter(subredditListItems)
        recyclerView.adapter = adapter

        getSubredditSearchFragment()
    }

    override fun onDestroy() {
        super.onDestroy()

        val actionBar = (activity as AppCompatActivity).supportActionBar
        actionBar?.show()
    }

    fun getSubredditSearchFragment() {
        CoroutineScope(Dispatchers.IO).launch {
            val responseString = searchSubreddit(mainInstance.access_token)
            println(responseString)
            if (responseString != null) {
                withContext(Dispatchers.Main) {
                    val responseJson = JSONObject(responseString)
                    val children = responseJson.getJSONObject("data").getJSONArray("children")
                    println(children.toString())
                    for (i in 0 until children.length()) {
                        val childData = children.getJSONObject(i).getJSONObject("data")
                        val subreddit = SubredditChildrenList(
                            childData.getString("display_name_prefixed"),
                            childData.getString("user_is_subscriber"),
                            childData.getString("icon_img"),
                            childData.getString("subscribers"),
                            childData.getString("id")
                        )
                        subredditListItems.add(subreddit)
                    }
                        adapter = SubredditListAdapter(subredditListItems)
                        binding.rvSubredditList.adapter = adapter
                    adapter.notifyDataSetChanged()
                }
            }
        }
    }
}
