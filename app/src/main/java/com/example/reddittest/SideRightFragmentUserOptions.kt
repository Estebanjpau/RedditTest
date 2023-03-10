package com.example.reddittest

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.reddittest.api.AccessTokenListener
import com.example.reddittest.api.CheckAvailableUsername
import com.example.reddittest.databinding.FragmentSideRightUserOptionsBinding

val handler = Handler(Looper.getMainLooper())

class SideRightFragmentUserOptions : Fragment(), CheckAvailableUsername, AccessTokenListener {

    lateinit var binding: FragmentSideRightUserOptionsBinding

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

        mainInstance = activity as MainActivity

        binding.btSruocontinue.setOnClickListener {
            loginUser = binding.etSruouser.text.toString()
            passwordUser = binding.etSruopassword.text.toString()

            try {getAvailableUsername(loginUser) { isAvailable ->
                    if (isAvailable) {
                        handler.post {
                            Toast.makeText(binding.btSruocontinue.context, "El usuario ingresado no es válido", Toast.LENGTH_SHORT).show()
                        }
                        loginUser = ""

                    } else {
                        println(loginUser)
                        if (passwordUser == ""){
                            handler.post {
                                Toast.makeText(binding.btSruocontinue.context, "Debes ingresar la contraseña para este usuario", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                }
            } catch (e: Exception){
                handler.post {
                    Toast.makeText(binding.btSruocontinue.context, "Algo no funcionó, verifica tu conexión a internet", Toast.LENGTH_SHORT).show()
                }
            }

            if(loginUser != "" && passwordUser != ""){
                mainInstance.loginAccount()
            }
            binding.btSruocontinue.clearFocus()
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() = SideRightFragmentUserOptions()
    }

    override fun onAccessTokenFetched(getAccessToken: String?) {
        if (getAccessToken != null) {
            mainInstance.access_token = getAccessToken
        } else {
            Toast.makeText(context, "No se pudo obtener el token de acceso", Toast.LENGTH_SHORT).show()
        }
    }
}