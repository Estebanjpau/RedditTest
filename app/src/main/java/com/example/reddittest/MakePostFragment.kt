package com.example.reddittest

import android.app.Activity
import android.content.Intent
import android.content.res.ColorStateList
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.reddittest.api.ManagePost
import com.example.reddittest.data_model.Rule
import com.example.reddittest.databinding.FragmentMakePostBinding
import com.example.reddittest.make_post_adapter.RuleListAdapter
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject

class MakePostFragment : Fragment(), ManagePost {

    private lateinit var binding: FragmentMakePostBinding
    private lateinit var mainInstance: MainActivity

    lateinit var bottomSheetDialog : BottomSheetDialog

    lateinit var ruleListAdapter : RuleListAdapter
    lateinit var titlePost: String
    lateinit var bodyPost: String
    lateinit var urlPost: String
    lateinit var subreddit : String
    var subredditRules = mutableSetOf<Rule>()
    lateinit var subredditImg : String

    private var selectedImageUri: Uri? = null

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

        binding.tvSubredditSelectedRules.setOnClickListener {
            bottomSheetDialog = BottomSheetDialog(requireContext())
            val parentLayout = binding.clMenuRules.parent as ViewGroup
            parentLayout.removeView(binding.clMenuRules)
            bottomSheetDialog.setContentView(binding.clMenuRules)
            binding.clMenuRules.visibility = View.VISIBLE
            bottomSheetDialog.show()

            CoroutineScope(Dispatchers.IO).launch {
                var subredditRuleResponse = getSubredditRules(subreddit).toString()
                var jsonresponse = JSONObject(subredditRuleResponse ?: "")
                var rules = jsonresponse.getJSONArray("rules")
                if (rules.length() == 0) {
                    subredditRuleResponse = getSubredditRules("r/reddit").toString()
                    jsonresponse = JSONObject(subredditRuleResponse ?: "")
                    rules = jsonresponse.getJSONArray("rules")
                }
                subredditRules.clear()
                withContext(Dispatchers.Main) {
                        for (i in 0 until rules.length()) {
                            val ruleObject = rules.getJSONObject(i)
                            val ruleList = Rule(
                                ruleObject.getString("short_name"),
                                ruleObject.getString("description")
                            )
                            subredditRules.add(ruleList)
                        }
                    ruleListAdapter = RuleListAdapter(subredditRules.toMutableList(), requireContext())
                    ruleListAdapter.notifyDataSetChanged()

                    binding.rvListofRules.layoutManager = LinearLayoutManager(activity)
                    binding.rvListofRules.adapter = ruleListAdapter
                }
            }
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

        val pickImageLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let {
                selectedImageUri = it
                binding.ivLoadimagePost.setImageURI(it)
            }
        }

        binding.ivLoadimagePost.setOnClickListener {
            pickImageLauncher.launch("image/*")
        }

        binding.btContinueToPost.setOnClickListener {
            titlePost = binding.etTitlePost.text.toString()
            bodyPost = binding.etBodyPost.text.toString()
            urlPost = binding.etUrlPost.text.toString()

            if (subreddit == "") {
                launchSearchSubredditFragment()

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

        if (bottomSheetDialog.isShowing) {
            bottomSheetDialog.dismiss()
        }

        val actionBar = (activity as AppCompatActivity).supportActionBar
        actionBar?.show()
    }

    fun showSubredditSelect(){
        binding.llSubredditSelected.visibility = View.VISIBLE
        binding.llSubredditSelected.setOnClickListener {
            launchSearchSubredditFragment()
        }
    }

    fun launchSearchSubredditFragment(){
        parentFragmentManager.beginTransaction()
            .setCustomAnimations(R.anim.enter_from_background_to_front,0,0,R.anim.enter_from_background_to_front)
            .add(R.id.fl_fragmentMKcontainer, mainInstance.searchSubredditInstance)
            .addToBackStack(null)
            .commit()
    }

    private fun checkFields() {
        val title = binding.etTitlePost.text.toString().trim()

        if (title.isNotEmpty()) {
            binding.btContinueToPost.isEnabled = true
            binding.btContinueToPost.backgroundTintList =
                ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.blue))
            binding.btContinueToPost.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
        } else {
            binding.btContinueToPost.isEnabled = false
            binding.btContinueToPost.backgroundTintList =
                ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.grey_dark))
            binding.btContinueToPost.setTextColor(
                ContextCompat.getColor(requireContext(), R.color.grey_semidark))
        }
    }
    fun loadSubredditSelected() {
        if (subredditImg != "") {
            Glide.with(binding.ivSubredditSelected.context).load(subredditImg).circleCrop()
                .into(binding.ivSubredditSelected)
        }
        binding.tvSubredditSelected.text = subreddit
    }
}