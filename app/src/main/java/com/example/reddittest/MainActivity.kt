package com.example.reddittest

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.reddittest.adapter.PostModelAdapter
import com.example.reddittest.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initRecyclerView()
    }

<<<<<<< HEAD
    private fun initRecyclerView(){
=======
    fun initRecyclerView(){
>>>>>>> bc0102f435b3e351ce661c4056cd856714a04da8

        binding.rvFeed.layoutManager = LinearLayoutManager(this)
        binding.rvFeed.adapter = PostModelAdapter(PostProvider.providerList)
    }
}