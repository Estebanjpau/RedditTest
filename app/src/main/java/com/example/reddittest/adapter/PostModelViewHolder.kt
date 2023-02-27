package com.example.reddittest.adapter

import android.net.Uri
import android.view.View
import android.widget.MediaController
import android.widget.Toast
import android.widget.VideoView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.example.reddittest.PostModel
import com.example.reddittest.PostUtils
import com.example.reddittest.R
import com.example.reddittest.databinding.ItemPostBinding


class PostModelViewHolder(view: View) : ViewHolder(view) {

    private val binding = ItemPostBinding.bind(view)

    fun paint(postExample: PostModel) {

        val postTimeAgo = postExample.created_at.toFloat().div(1L).let {
            PostUtils.getTimeAgo(it.toInt())
        }

        val postCounterCommentResume = postExample.counterComments.let {
            PostUtils.resumeCounterNumber(it.toInt())
        }

        val postCounterVotesResume = postExample.counterVotes.let {
            PostUtils.resumeCounterNumber(it.toInt())
        }


        binding.tvUserId.text = postExample.userId
        binding.tvAlias.text = postExample.alias
        binding.tvTitle.text = postExample.title
        binding.tvTimeAgo.text = postTimeAgo
        Glide.with(binding.ivUserPhoto.context).load(postExample.userPhoto)
            .into(binding.ivUserPhoto)
        Glide.with(binding.ivPostImage.context).load(postExample.postPhoto)
            .into(binding.ivPostImage)

        var videoView: VideoView = binding.vvPostVideo
        val mediaController =  MediaController(videoView.context)
        mediaController.setMediaPlayer(videoView)
        mediaController.setAnchorView(videoView)
        videoView.setMediaController(mediaController)
        if (postExample.secureMedia?.RedditVideo?.urlVideo != null){
            val paramsPostVideo = videoView.layoutParams
            paramsPostVideo.height = 1200
            videoView.layoutParams = paramsPostVideo

            val socialBar = binding.clsocialbar
            val paramsSocialBar = socialBar.layoutParams as ConstraintLayout.LayoutParams
            paramsSocialBar.startToStart = ConstraintLayout.LayoutParams.PARENT_ID
            paramsSocialBar.topToBottom = R.id.vvPostVideo

        }

        binding.vvPostVideo.setVideoURI(Uri.parse(postExample.secureMedia?.RedditVideo?.urlVideo.toString()))

        if (postExample.secureMedia?.RedditVideo?.urlVideo.toString() == "null"){
            binding.tvTitle.setOnClickListener{Toast.makeText(videoView.context,"Sin Url",Toast.LENGTH_LONG).show()}
        } else {
            binding.tvTitle.setOnClickListener {
                Toast.makeText(
                    videoView.context,
                    (postExample.secureMedia?.RedditVideo?.urlVideo.toString()),
                    Toast.LENGTH_LONG
                ).show()
            }
        }
        //binding.tvTitle.text = postExample.secureMedia?.RedditVideo?.urlVideo.toString()

        binding.tvCounterVotes.text = postCounterVotesResume
        binding.tvCounterComment.text = postCounterCommentResume
    }

}