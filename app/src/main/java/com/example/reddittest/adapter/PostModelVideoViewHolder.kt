package com.example.reddittest.adapter

import android.net.Uri
import android.view.View
import android.widget.MediaController
import android.widget.Toast
import android.widget.VideoView
import com.example.reddittest.MainActivity
import com.example.reddittest.PostModel
import com.example.reddittest.PostUtils
import com.example.reddittest.api.GetVoteDir
import com.example.reddittest.api.postVote
import com.example.reddittest.databinding.ItemPostBinding
import kotlinx.coroutines.*

class PostModelVideoViewHolder(view: View, private val mainInstance: MainActivity) : PostModelViewHolder(view, mainInstance) {

    private val binding = ItemPostBinding.bind(view)
    override var postVoteDir = ""

    override fun paint(postExample: PostModel) {

        val postTimeAgo = postExample.created_at.toFloat().div(1L).let {
            PostUtils.getTimeAgo(it.toInt())
        }

        val postCounterCommentResume = postExample.counterComments.let {
            PostUtils.resumeCounterNumber(it.toInt())
        }

        val postCounterVotesResume = postExample.counterVotes.let {
            PostUtils.resumeCounterNumber(it.toInt())
        }

        fun refreshVote() {
            if(mainInstance.access_token.isNotEmpty()) {
                val getVote = GetVoteDir(postExample.subreddit, postExample.postId, mainInstance.access_token, postExample.userId)
                val deferredResult = GlobalScope.async {
                    getVote.GetPostDirInBackground().await()
                }
                postVoteDir = runBlocking {
                    deferredResult.await()
                }
            }
        }

        refreshVote()

        if (postVoteDir == "null"){
            binding.tbUpVote.isChecked = false
            binding.tbDownVote.isChecked = false
        }

        if (postVoteDir == "true"){
            binding.tbUpVote.isChecked = true
        }

        if (postVoteDir == "false"){
            binding.tbDownVote.isChecked = true
        }

        binding.tbUpVote.setOnClickListener {
            if (postVoteDir == "null" || postVoteDir == "false") {
                val coroutineScope = CoroutineScope(Dispatchers.IO)
                coroutineScope.launch {
                    val success = postVote(postExample.fullnamePostId, "1", mainInstance.access_token)
                    withContext(Dispatchers.IO) {
                        if (success) {
                            postVoteDir = "true"
                            binding.tbUpVote.isChecked = true
                            binding.tbDownVote.isChecked = false
                        }
                    }
                }

            } else if (postVoteDir == "true"){
                val coroutineScope = CoroutineScope(Dispatchers.IO)
                coroutineScope.launch {
                    val success = postVote(postExample.fullnamePostId, "0", mainInstance.access_token)
                    withContext(Dispatchers.IO) {
                        if (success) {
                            postVoteDir = "null"
                            binding.tbUpVote.isChecked = false
                            binding.tbDownVote.isChecked = false
                        }
                    }
                }
            }
        }

        binding.tbDownVote.setOnClickListener {
            if (postVoteDir == "null" || postVoteDir == "true") {
                val coroutineScope = CoroutineScope(Dispatchers.IO)
                coroutineScope.launch {
                    val success = postVote(postExample.fullnamePostId, "-1", mainInstance.access_token)
                    withContext(Dispatchers.IO) {
                        if (success) {
                            postVoteDir = "false"
                            binding.tbDownVote.isChecked = true
                            binding.tbUpVote.isChecked = false
                        }
                    }
                }

            } else if(postVoteDir == "false"){
                val coroutineScope = CoroutineScope(Dispatchers.IO)
                coroutineScope.launch {
                    val success = postVote(postExample.fullnamePostId, "0", mainInstance.access_token)
                    withContext(Dispatchers.IO) {
                        if (success) {
                            postVoteDir = "null"
                            binding.tbUpVote.isChecked = false
                            binding.tbDownVote.isChecked = false
                        }
                    }
                }
            }
        }

        binding.tvUserId.text = postExample.userId
        binding.tvAlias.text = postExample.subreddit
        binding.tvTitle.text = postExample.title
        binding.tvTimeAgo.text = postTimeAgo

        var videoView: VideoView = binding.vvPostVideo
        val mediaController = MediaController(videoView.context)
        mediaController.setMediaPlayer(videoView)
        mediaController.setAnchorView(videoView)
        videoView.setMediaController(mediaController)
        if (postExample.secureMedia?.RedditVideo?.urlVideo != null) {
            val paramsPostVideo = videoView.layoutParams

            paramsPostVideo.width = mainInstance.displayWidth
            paramsPostVideo.height = mainInstance.displayHeight
            videoView.layoutParams = paramsPostVideo
            binding.tvCounterVotes.text = postCounterVotesResume
            binding.tvCounterComment.text = postCounterCommentResume
        }

        binding.vvPostVideo.setVideoURI(Uri.parse(postExample.secureMedia?.RedditVideo?.urlVideo.toString()))

        if (postExample.secureMedia?.RedditVideo?.urlVideo.toString() == "null") {
            binding.tvTitle.setOnClickListener {
                Toast.makeText(videoView.context, "Sin Url", Toast.LENGTH_LONG).show()
            }
        } else {
            binding.tvTitle.setOnClickListener {
                Toast.makeText(
                    videoView.context,(postExample.secureMedia?.RedditVideo?.urlVideo.toString()), Toast.LENGTH_LONG).show()
            }
        }

    }
}