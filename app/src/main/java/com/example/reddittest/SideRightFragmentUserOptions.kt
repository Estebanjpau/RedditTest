package com.example.reddittest

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.reddittest.api.AccessTokenListener
import com.example.reddittest.api.CheckAvailableUsername
import com.example.reddittest.databinding.FragmentSideRightUserOptionsBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

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

            if (loginUser.isEmpty() || passwordUser.isEmpty()) {
                handler.post {
                    Toast.makeText(binding.btSruocontinue.context, "Debes ingresar el nombre de usuario y la contraseña", Toast.LENGTH_SHORT).show()
                }
            } else {
                try {
                    getAvailableUsername(loginUser) { isAvailable ->
                        if (isAvailable) {
                            handler.post {
                                Toast.makeText(binding.btSruocontinue.context, "El usuario ingresado no es válido", Toast.LENGTH_SHORT).show()
                            }
                        } else {
                            CoroutineScope(Dispatchers.Main).launch {
                                loginAccount()
                                mainInstance.checkFragmentInit()
                            }
                        }
                    }
                } catch (e: Exception) {
                    handler.post {
                        Toast.makeText(binding.btSruocontinue.context, "Algo no funcionó, verifica tu conexión a internet", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    override fun onAccessTokenFetched(getAccessToken: String?) {
        if (getAccessToken != null) {
            mainInstance.access_token = getAccessToken
        } else {
            Toast.makeText(context, "No se pudo obtener el token de acceso", Toast.LENGTH_SHORT).show()
        }
    }

    fun loginAccount() {
        mainInstance.fetchAccessTokenTask.setAccessTokenListener(object : AccessTokenListener {
            override fun onAccessTokenFetched(getAccessToken: String?) {
                if (!getAccessToken.isNullOrEmpty()) {
                    mainInstance.access_token = getAccessToken
                    mainInstance.initRecyclerView()
                    mainInstance.getTop()
                    mainInstance.unlockFragmentSRUO()
                    mainInstance.hideFragmentSRUO()
                } else {
                    Toast.makeText(mainInstance, "Contraseña equivocada", Toast.LENGTH_SHORT).show()
                    binding.etSruopassword.setText("")
                }
            }
        })
        mainInstance.fetchAccessTokenTask.execute("4ICQyJNWimrCLb7plegtvg", "cEA_Ztz1MH99kzs8gk5iJJdG2YSQTA", loginUser, passwordUser)
    }
}

