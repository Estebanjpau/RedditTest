package com.example.reddittest

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.ContentResolver
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.ColorStateList
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
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
import java.io.File
import java.io.FileOutputStream

class MakePostFragment : Fragment(), ManagePost{

    companion object { private const val CAMERA_PERMISSION_REQUEST_CODE = 100 }

    private lateinit var binding: FragmentMakePostBinding
    private lateinit var mainInstance: MainActivity
    lateinit var bottomSheetDialog: BottomSheetDialog
    lateinit var bodyPost: String
    lateinit var ruleListAdapter: RuleListAdapter
    private lateinit var selectedImageBitmap: Bitmap
    lateinit var subreddit: String
    lateinit var subredditImg: String
    lateinit var titlePost: String
    lateinit var urlPost: String

    var kind = "self"
    lateinit var imageFile : File

    var subredditRules = mutableSetOf<Rule>()

    val startForResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            result: ActivityResult ->
        if(result.resultCode == Activity.RESULT_OK){
            val intent = result.data
            val imageBitMap = intent?.extras?.get("data") as Bitmap
            if (binding.llContentMedia.visibility == View.GONE){ binding.llContentMedia.visibility = View.VISIBLE }
            binding.ivPreviewMedia.setImageBitmap(imageBitMap)
            selectedImageBitmap = imageBitMap
            kind = "image"
        }
    }

    private val pickImageLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            selectedImageBitmap = getBitmapFromUri(uri)
            if (binding.llContentMedia.visibility == View.GONE){ binding.llContentMedia.visibility = View.VISIBLE }
            binding.ivPreviewMedia.setImageBitmap(selectedImageBitmap)
            kind = "image"
        }
    }

    private fun getBitmapFromUri(uri: Uri): Bitmap {
        val inputStream = requireContext().contentResolver.openInputStream(uri)
        return BitmapFactory.decodeStream(inputStream)
    }

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

        binding.ivDeleteMediaTaken.setOnClickListener {
            binding.ivPreviewMedia.setImageBitmap(null)
            binding.llContentMedia.visibility = View.GONE
            kind = "self"
        }

        binding.ivLoadimagePost.setOnClickListener {
            val options = arrayOf<CharSequence>("Tomar Foto", "Elegir de la galería", "Cancelar")
            val builder = AlertDialog.Builder(requireContext())
            builder.setTitle("Elige una opción")
            builder.setItems(options) { dialog, item ->
                when {
                    options[item] == "Tomar Foto" -> {
                        if (ContextCompat.checkSelfPermission(
                                requireContext(),
                                Manifest.permission.CAMERA
                            ) != PackageManager.PERMISSION_GRANTED
                        ) {
                            ActivityCompat.requestPermissions(
                                requireActivity(),
                                arrayOf(Manifest.permission.CAMERA),
                                CAMERA_PERMISSION_REQUEST_CODE
                            )
                        } else {
                             startForResult.launch(Intent(MediaStore.ACTION_IMAGE_CAPTURE))
                        }
                    }
                    options[item] == "Elegir de la galería" -> {
                        pickImageLauncher.launch("image/*")
                    }
                    options[item] == "Cancelar" -> {
                        dialog.dismiss()
                    }
                }
            }
            builder.show()
        }

        binding.btContinueToPost.setOnClickListener {
            titlePost = binding.etTitlePost.text.toString()
            bodyPost = binding.etBodyPost.text.toString()
            urlPost = binding.etUrlPost.text.toString()

            if (subreddit == "") {
                launchSearchSubredditFragment()

            } else if (titlePost != "") {
                if(kind == "image"){
                    val cacheDir = context?.cacheDir
                    imageFile = File.createTempFile("image", ".jpg", cacheDir)

                    val outputStream = FileOutputStream(imageFile)
                    selectedImageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
                    outputStream.flush()
                    outputStream.close()
                }

                CoroutineScope(Dispatchers.Main).launch {
                    try {
                        makePost(mainInstance.access_token, titlePost, bodyPost, urlPost,imageFile,null,kind,subreddit)
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