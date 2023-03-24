package com.example.reddittest

import com.google.gson.annotations.SerializedName

data class SubredditListResponse(
    @SerializedName("kind")
    val king : String,
    @SerializedName("data")
    val data : ResponseSubredditList
)

data class ResponseSubredditList(
    @SerializedName("children")
    val children: List<SubredditListData>
)

data class SubredditListData(
    @SerializedName("data")
    val data : SubredditChildrenList
)

data class SubredditChildrenList(
    @SerializedName("display_name_prefixed")
    val subredditPrefixed:String,
    @SerializedName("user_is_subscriber")
    val CheckSuscription:String,
    @SerializedName("icon_img")
    val subredditImage:String,
    @SerializedName("subscribers")
    val suscribers:String,
    @SerializedName("id")
    val id:String
)

