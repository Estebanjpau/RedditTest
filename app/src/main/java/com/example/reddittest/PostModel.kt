package com.example.reddittest

import com.google.gson.annotations.SerializedName

data class TopResponse(
    @SerializedName("kind")
    val kind: String,
    @SerializedName("data")
    val data: TopResponseData
)

data class TopResponseData(
    @SerializedName("children")
    val children: List<TopDataItem>
)

data class TopDataItem(
    @SerializedName("data")
    val data: PostModel
)

data class PostModel(
    @SerializedName("subreddit")
    val alias: String,
    @SerializedName("score")
    val counterVotes: String,
    @SerializedName("num_comments")
    val counterComments: String,
    @SerializedName("id")
    val postId: String,
    @SerializedName("author")
    val userId: String,
    @SerializedName("created_utc")
    val created_at: String,
    @SerializedName("userPhoto")
    val userPhoto: String,
    @SerializedName("title")
    val title: String,
    @SerializedName("url")
    val postPhoto: String,
    @SerializedName("media")
    val secureMedia: Media?
)

data class Media(
    @SerializedName("reddit_video")
    val RedditVideo: Video?
)

data class Video(
    @SerializedName("fallback_url")
    val urlVideo: String
)






