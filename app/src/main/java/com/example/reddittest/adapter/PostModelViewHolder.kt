package com.example.reddittest.adapter

import android.net.Uri
import android.view.View
import android.widget.MediaController
import android.widget.VideoView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.example.reddittest.PostModel
import com.example.reddittest.PostUtils
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
        videoView.setMediaController(mediaController)
        binding.vvPostVideo.setVideoURI(Uri.parse(postExample.secureMedia?.RedditVideo?.urlVideo.toString()))


        //binding.tvTitle.text = postExample.secureMedia?.RedditVideo?.urlVideo.toString()

        binding.tvCounterVotes.text = postCounterVotesResume
        binding.tvCounterComment.text = postCounterCommentResume
    }

}