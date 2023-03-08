package com.example.reddittest

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.reddittest.api.AccessTokenListener
import com.example.reddittest.api.CheckAvailableUsername
import com.example.reddittest.api.FetchAccessTokenTask
import com.example.reddittest.databinding.FragmentSideRightUserOptionsBinding
import okhttp3.internal.platform.Jdk9Platform.Companion.isAvailable

private val ARG_PARAM1 = "param1"
private val ARG_PARAM2 = "param2"
val handler = Handler(Looper.getMainLooper())

class SideRightFragmentUserOptions() : Fragment(), CheckAvailableUsername, AccessTokenListener{

    private lateinit var binding: FragmentSideRightUserOptionsBinding

    private var loginUser = ""
    private var passwordUser = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSideRightUserOptionsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btSruocontinue.setOnClickListener {
            loginUser = binding.etSruouser.text.toString()
            passwordUser = binding.etSruopassword.text.toString()

            try {
                getAvailableUsername(loginUser){
                    if (isAvailable == false){
                        loginUser
                    } else if (isAvailable == true){
                        loginUser = ""
                        handler.post { Toast.makeText(binding.btSruocontinue.context, "El usuario ingresado no existe", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            } catch (e: java.lang.Exception){
                handler.post {
                    Toast.makeText(binding.btSruocontinue.context, "Algo no funcionó, verifica tu conexión a internet", Toast.LENGTH_SHORT).show()
                }
            }

            if (loginUser != ""){
                val fetchAccessTokenTask = FetchAccessTokenTask(this)
                fetchAccessTokenTask.execute("4ICQyJNWimrCLb7plegtvg", "cEA_Ztz1MH99kzs8gk5iJJdG2YSQTA")

            }
        }
    }
}

