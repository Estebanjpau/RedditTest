package com.example.reddittest

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.reddittest.adapter.PostModelImageViewHolder
import com.example.reddittest.adapter.PostModelViewHolder
import com.example.reddittest.api.AccessTokenListener
import com.example.reddittest.api.CheckAvailableUsername
import com.example.reddittest.databinding.FragmentSideRightUserOptionsBinding

val handler = Handler(Looper.getMainLooper())

class SideRightFragmentUserOptions : Fragment(), CheckAvailableUsername, AccessTokenListener {

    private lateinit var binding: FragmentSideRightUserOptionsBinding

    var loginUser = ""
    var passwordUser = ""
    private lateinit var mainInstance: MainActivity

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
                getAvailableUsername(loginUser) { isAvailable ->
                    if (isAvailable) {
                        handler.post {
                            Toast.makeText(binding.btSruocontinue.context, "El usuario ingresado no es válido", Toast.LENGTH_SHORT).show()
                        }
                        loginUser = ""

                    } else {
                        println(loginUser)
                    }
                }
            } catch (e: Exception){
                handler.post {
                    Toast.makeText(binding.btSruocontinue.context, "Algo no funcionó, verifica tu conexión a internet", Toast.LENGTH_SHORT).show()
                }
            }

            if(loginUser != ""){
                //despues de validar el user, lanzar el getAccessToken y agregar inicio de sesion si el resultado fue exitoso o no
                if (mainInstance.access_token.isNotEmpty()) {

                }
            }
        }
    }

    companion object {
        fun newInstance(mainInstance: MainActivity): SideRightFragmentUserOptions {
            val fragment = SideRightFragmentUserOptions()
            fragment.mainInstance = mainInstance
            return fragment
        }
    }
}




