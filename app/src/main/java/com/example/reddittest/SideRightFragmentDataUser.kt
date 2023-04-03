package com.example.reddittest

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.reddittest.api.AccessTokenListener
import com.example.reddittest.api.GetUserData
import com.example.reddittest.databinding.FragmentSideRightDataUserBinding
import kotlinx.coroutines.*

class SideRightFragmentDataUser : Fragment(), GetUserData, AccessTokenListener {

    lateinit var display_name_prefixed : String
    lateinit var total_karma : String
    lateinit var created_utc : String
    lateinit var coins : String
    lateinit var snoovatar_img : String

    lateinit var mainInstance: MainActivity
    lateinit var binding: FragmentSideRightDataUserBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainInstance = activity as MainActivity
        mainInstance.onAccessTokenFetched(mainInstance.access_token)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSideRightDataUserBinding.inflate(inflater, container, false)

        val coroutineScope = CoroutineScope(Dispatchers.Main)

        coroutineScope.launch {
            while (mainInstance.access_token == "") {
                delay(200)
            }

            val displaynameprefixed = async {
                refreshDataUsernameInbg().await()
            }

            val totalkarma = async {
                refreshDataUserDataInbg("total_karma").await()
            }

            val createdutc = async {
                refreshDataUserDataInbg("created_utc").await()
            }

            var ucoins = async {
                refreshDataUserDataInbg("coins").await()
            }

            val snoovatarimg = async {
                refreshDataUserDataInbg("snoovatar_img").await()
            }

            display_name_prefixed = displaynameprefixed.await()
            total_karma = totalkarma.await()
            created_utc = createdutc.await()
            coins = ucoins.await()
            snoovatar_img = snoovatarimg.await()

            binding.tvUsername.text = display_name_prefixed
            binding.tvKarmaCounter.text = total_karma
            binding.tvOptionCoins.text = "${coins} monedas"


            val postTimeAgo = created_utc.toFloat().div(1L).let {
                PostUtils.getTimeAgo(it.toInt())
            }

            binding.tvTimeInReddit.text = postTimeAgo
            Glide.with(binding.ivSnoovatar.context).load(snoovatar_img)
                .into(binding.ivSnoovatar)
            binding.ivSnoovatar.bringToFront();
        }

        return binding.root
    }

    override fun onAccessTokenFetched(getAccessToken: String?) {

    }

    fun refreshDataUsernameInbg(): Deferred<String> {
        return CoroutineScope(Dispatchers.IO).async {
            try {
                getLoggedUserSubreddit(mainInstance.access_token)
            } catch (e:Exception){
                ""
            }
        }
    }

    fun refreshDataUserDataInbg(data:String): Deferred<String> {
        return CoroutineScope(Dispatchers.IO).async {
            try {
                getLoggedUserData(mainInstance.access_token,data)
            } catch (e:Exception){
                ""
            }
        }
    }
}