package com.example.reddittest

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.reddittest.api.AccessTokenListener
import com.example.reddittest.api.GetUserData
import com.example.reddittest.databinding.FragmentSideRightDataUserBinding
import kotlinx.coroutines.*

class SideRightFragmentDataUser : Fragment(), GetUserData, AccessTokenListener {

    lateinit var mainInstance: MainActivity
    lateinit var binding: FragmentSideRightDataUserBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainInstance = activity as MainActivity
        mainInstance.onAccessTokenFetched(mainInstance.access_token)
    }

    @OptIn(DelicateCoroutinesApi::class)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSideRightDataUserBinding.inflate(inflater, container, false)

        val deferredResult = GlobalScope.async {
            refreshDataUserInbg().await()
        }

        GlobalScope.launch(Dispatchers.Main) {
            binding.tvUsername.text = deferredResult.await()
        }

        return binding.root
    }

    override fun onAccessTokenFetched(getAccessToken: String?) {

    }

    fun refreshDataUserInbg(): Deferred<String> {
        return CoroutineScope(Dispatchers.IO).async {
            try {
                getLoggedUsername(mainInstance.access_token)
            } catch (e:Exception){
                "u/Username"
            }
        }
    }
}