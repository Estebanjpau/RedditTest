package com.example.reddittest

import android.content.res.ColorStateList
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.reddittest.api.ManagePost
import com.example.reddittest.databinding.FragmentMakePostBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MakePostFragment : Fragment(), ManagePost {

    private lateinit var binding: FragmentMakePostBinding
    private lateinit var mainInstance: MainActivity
    lateinit var titlePost: String
    lateinit var bodyPost: String
    lateinit var urlPost: String
    lateinit var subreddit : String

    var kind = "self"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentMakePostBinding.inflate(inflater, container, false)

        mainInstance = activity as MainActivity

        val actionBar = (activity as AppCompatActivity).supportActionBar
        actionBar?.hide()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val constraintUserOptions = binding.clFragmentMP
        val buttonContinue = binding.btContinueToPost

        binding.etTitlePost.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                checkFields()
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        binding.ivClosePostFragment.setOnClickListener {
            requireActivity().supportFragmentManager.beginTransaction()
                .remove(this)
                .commit()

            val actionBar = (activity as AppCompatActivity).supportActionBar
            actionBar?.show()
        }

        val rootLayout = binding.root
        rootLayout.viewTreeObserver.addOnGlobalLayoutListener {
            val heightDiff = rootLayout.rootView.height - constraintUserOptions.height
            if (heightDiff > 0) {
                buttonContinue.translationY = 0f
            } else {
                buttonContinue.translationY = -heightDiff.toFloat()
            }
        }

        subreddit = ""

        binding.ivLoadlinkPost.setOnClickListener {
            binding.llUrlPost.visibility = View.VISIBLE
            kind = "link"
        }

        binding.ibCloseUrlOption.setOnClickListener {
            kind = "self"
            binding.llUrlPost.visibility = View.GONE
        }

        binding.btContinueToPost.setOnClickListener {
            titlePost = binding.etTitlePost.text.toString()
            bodyPost = binding.etBodyPost.text.toString()
            urlPost = binding.etUrlPost.text.toString()

            if (subreddit == "") {
                val fragmentSS = SearchSubredditFragment()
                parentFragmentManager.beginTransaction()
                    .setCustomAnimations(R.anim.enter_from_background_to_front,0,0,R.anim.enter_from_background_to_front)
                    .add(R.id.fl_fragmentSScontainer, fragmentSS)
                    .addToBackStack(null)
                    .commit()

            } else if (titlePost != "") {
                CoroutineScope(Dispatchers.Main).launch {
                    try {
                        makePost(mainInstance.access_token, titlePost, bodyPost, urlPost,kind)
                        Toast.makeText(binding.clFragmentMP.context,"Publicacion exitosa",Toast.LENGTH_SHORT).show()
                    } catch (e:Exception){
                     Toast.makeText(binding.clFragmentMP.context,"Error en la peticion",Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        val actionBar = (activity as AppCompatActivity).supportActionBar
        actionBar?.show()
    }

    private fun checkFields() {
        val title = binding.etTitlePost.text.toString().trim()

        if (title.isNotEmpty()) {
            binding.btContinueToPost.isEnabled = true
            binding.btContinueToPost.backgroundTintList =
                ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.blue))
            binding.btContinueToPost.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.white
                )
            )
        } else {
            binding.btContinueToPost.isEnabled = false
            binding.btContinueToPost.backgroundTintList =
                ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.grey_dark))
            binding.btContinueToPost.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.grey_semidark
                )
            )
        }
    }
}
