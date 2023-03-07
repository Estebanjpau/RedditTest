package com.example.reddittest

import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.reddittest.adapter.PostModelAdapter
import com.example.reddittest.api.APIService
import com.example.reddittest.api.AccessTokenListener
import com.example.reddittest.api.FetchAccessTokenTask
import com.example.reddittest.databinding.ActivityMainBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class MainActivity : AppCompatActivity(), AccessTokenListener {

    private lateinit var binding: ActivityMainBinding
    private lateinit var adapter: PostModelAdapter
    private val posts = mutableListOf<PostModel>()
    private lateinit var mainInstance : MainActivity

    var access_token = ""
    var displayWidth = 1200
    var displayHeight = 1200

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val fetchAccessTokenTask = FetchAccessTokenTask(this)
        fetchAccessTokenTask.execute("4ICQyJNWimrCLb7plegtvg", "cEA_Ztz1MH99kzs8gk5iJJdG2YSQTA")

        getDarkModeWindow()

        mainInstance = this
        displayWidth = this.resources.displayMetrics.widthPixels
        displayHeight = this.resources.displayMetrics.heightPixels

        initRecyclerView()
        getTop()
    }

    private fun initRecyclerView() {
        adapter = PostModelAdapter(posts, mainInstance)
        binding.rvFeed.layoutManager = LinearLayoutManager(this)
        binding.rvFeed.adapter = adapter
    }

    private fun getRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://api.reddit.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    private fun getTop() {
        CoroutineScope(Dispatchers.IO).launch {
            val call = getRetrofit().create(APIService::class.java)
                .getRedditTop()
            val response = call.body()
            if (call.isSuccessful) {
                runOnUiThread {
                    posts.addAll(response?.data?.children?.map { it.data } ?: listOf())
                    if (access_token.isNotEmpty()) {
                        adapter.notifyDataSetChanged()
                    }
                }
            }
        }
    }

    private fun getDarkModeWindow(){
        val window = this.window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        window.statusBarColor = this.resources.getColor(R.color.dark_mode)
        window.navigationBarColor = this.resources.getColor(R.color.dark_mode)
    }

    override fun onAccessTokenFetched(getAccessToken: String?) {
        access_token = getAccessToken.toString()
        if (posts.isNotEmpty()) {
            adapter.notifyDataSetChanged()
        }
    }
}