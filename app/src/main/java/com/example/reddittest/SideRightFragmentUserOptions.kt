package com.example.reddittest

import android.content.res.ColorStateList
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.reddittest.api.AccessTokenListener
import com.example.reddittest.api.CheckAvailableUsername
import com.example.reddittest.databinding.FragmentSideRightUserOptionsBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SideRightFragmentUserOptions : Fragment(), CheckAvailableUsername, AccessTokenListener {

    val handler = Handler(Looper.getMainLooper())

    lateinit var binding: FragmentSideRightUserOptionsBinding

    var loginUser = ""
    var passwordUser = ""
    private lateinit var mainInstance: MainActivity

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSideRightUserOptionsBinding.inflate(inflater, container, false)

        val constraintUserOptions = binding.clSRfragmentUserOptions
        val buttonContinue = binding.btSruocontinue

        val rootLayout = binding.root
        rootLayout.viewTreeObserver.addOnGlobalLayoutListener {
            val heightDiff = rootLayout.rootView.height - constraintUserOptions.height
            if (heightDiff > 0) {
                buttonContinue.translationY = 0f
            } else {
                buttonContinue.translationY = -heightDiff.toFloat()
            }
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mainInstance = activity as MainActivity

        binding.etSruouser.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                checkFields()
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        binding.etSruopassword.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                checkFields()
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        checkFields()

        var esteban = "estebanjpau"
        var contra = "wd1qi5fl"
        loginUser = binding.etSruouser.setText(esteban).toString()
        passwordUser = binding.etSruopassword.setText(contra).toString()

        binding.btSruocontinue.setOnClickListener {
            loginUser = binding.etSruouser.text.toString()
            passwordUser = binding.etSruopassword.text.toString()

            if (loginUser.isEmpty() && passwordUser.isEmpty()) {
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
                        }else {
                            CoroutineScope(Dispatchers.Main).launch {
                                loginAccount()
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

    private fun checkFields() {
        val user = binding.etSruouser.text.toString().trim()
        val password = binding.etSruopassword.text.toString().trim()

        if (user.isNotEmpty() && password.isNotEmpty()) {
            binding.btSruocontinue.isEnabled = true
            binding.btSruocontinue.backgroundTintList =
                ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.red))
            binding.btSruocontinue.setTextColor(ContextCompat.getColor(requireContext(),R.color.white))
        } else {
            binding.btSruocontinue.isEnabled = false
            binding.btSruocontinue.backgroundTintList =
                ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.grey_dark))
            binding.btSruocontinue.setTextColor(ContextCompat.getColor(requireContext(),R.color.grey_semidark))
        }
    }

    fun loginAccount() {
        mainInstance.fetchAccessTokenTask.setAccessTokenListener(object : AccessTokenListener {
            override fun onAccessTokenFetched(getAccessToken: String?) {
                if (!getAccessToken.isNullOrEmpty()) {
                    mainInstance.access_token = getAccessToken
                    if (mainInstance.access_token != "") {
                        mainInstance.initRecyclerView()
                        mainInstance.getTop()
                        mainInstance.unlockFragmentSRUO()
                        mainInstance.hideFragmentSRUO()
                        mainInstance.checkFragmentInit()
                    }
                } else {
                    Toast.makeText(mainInstance, "Contraseña equivocada", Toast.LENGTH_SHORT).show()
                    binding.etSruopassword.setText("")
                }
            }
        })
        mainInstance.fetchAccessTokenTask.execute("4ICQyJNWimrCLb7plegtvg", "cEA_Ztz1MH99kzs8gk5iJJdG2YSQTA", loginUser, passwordUser)
    }
}