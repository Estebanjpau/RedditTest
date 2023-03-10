package com.example.reddittest

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.*
import android.view.animation.AlphaAnimation
import android.widget.FrameLayout
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.reddittest.adapter.PostModelAdapter
import com.example.reddittest.api.APIService
import com.example.reddittest.api.AccessTokenListener
import com.example.reddittest.api.FetchAccessTokenTask
import com.example.reddittest.databinding.ActivityMainBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class MainActivity : AppCompatActivity(), AccessTokenListener {

    private lateinit var binding: ActivityMainBinding
    private lateinit var adapter: PostModelAdapter
    private val posts = mutableListOf<PostModel>()
    private lateinit var mainInstance : MainActivity

    val fragment = SideRightFragmentUserOptions()
    var access_token = ""
    var displayWidth = 1200
    var displayHeight = 1200

    val fetchAccessTokenTask = FetchAccessTokenTask()

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mainInstance = this
        displayWidth = this.resources.displayMetrics.widthPixels
        displayHeight = this.resources.displayMetrics.heightPixels

        getDarkModeWindow()

        initFragmentSRUO()
        showFragmentSRUO()
        LockFragmentSRUO()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_principal, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_item_fragmento -> {
                val fragment =
                    supportFragmentManager.findFragmentById(R.id.fl_SideRightFragmenUserOptions)
                        ?: return true
                if (fragment.isVisible) {
                    if(access_token.isNotEmpty()) {
                        Handler(Looper.getMainLooper()).postDelayed({
                            hideFragmentSRUO()
                        }, 70)
                        val constraintLayout = findViewById<ConstraintLayout>(R.id.cl_ContentSRUO)
                        val animation = AlphaAnimation(1f, 0f)
                        animation.duration = 200
                        constraintLayout.startAnimation(animation)
                        constraintLayout.visibility = View.INVISIBLE
                    } else {
                        Toast.makeText(this,"Inicie sesión para continuar",Toast.LENGTH_SHORT).show()
                    }
                } else {
                    val constraintLayout = findViewById<ConstraintLayout>(R.id.cl_ContentSRUO)
                    val animation = AlphaAnimation(0f, 1f)
                    animation.duration = 250
                    Handler(Looper.getMainLooper()).postDelayed({
                        constraintLayout.startAnimation(animation)
                    }, 350)
                    showFragmentSRUO()
                    Handler(Looper.getMainLooper()).postDelayed(
                        {
                            constraintLayout.visibility = View.VISIBLE
                        }, 400
                    )
                }
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun initRecyclerView() {
        adapter = PostModelAdapter(posts, mainInstance)
        binding.rvFeed.layoutManager = LinearLayoutManager(this)
        binding.rvFeed.adapter = adapter
    }

    private fun getRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://api.reddit.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    private fun getTop() {
        CoroutineScope(Dispatchers.IO).launch {
            val call = getRetrofit().create(APIService::class.java)
                .getRedditTop()
            val response = call.body()
            if (call.isSuccessful) {
                runOnUiThread {
                    posts.addAll(response?.data?.children?.map { it.data } ?: listOf())
                    if (access_token.isNotEmpty()) {
                        adapter.notifyDataSetChanged()
                    }
                }
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onAccessTokenFetched(getAccessToken: String?) {
        access_token = getAccessToken.toString()
        if (posts.isNotEmpty()) {
            adapter.notifyDataSetChanged()
        }
    }

    fun hideSRUOfragmentOnClick(view: View) {
        val fragment = supportFragmentManager.findFragmentById(R.id.fl_SideRightFragmenUserOptions)
        if (fragment != null) {
            if (fragment.isVisible) {
                supportFragmentManager.beginTransaction()
                    .setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_right, R.anim.enter_from_right, R.anim.exit_to_right)
                    .hide(fragment)
                    .commit()
                val constraintLayout = findViewById<ConstraintLayout>(R.id.cl_ContentSRUO)
                val animation = AlphaAnimation(1f, 0f)
                animation.duration = 200
                constraintLayout.startAnimation(animation)
                constraintLayout.visibility = View.INVISIBLE
            }
        }
    }

    fun initFragmentSRUO() {
        supportFragmentManager.beginTransaction()
            .add(R.id.fl_SideRightFragmenUserOptions, fragment)
            .hide(fragment)
            .commit()
    }

    fun showFragmentSRUO() {
        if (!fragment.isVisible) {
            supportFragmentManager.beginTransaction()
                .setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_right, R.anim.enter_from_right, R.anim.exit_to_right)
                .show(fragment)
                .commit()
        }
    }

    fun hideFragmentSRUO() {
        if (fragment.isVisible) {
            supportFragmentManager.beginTransaction()
                .setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_right, R.anim.enter_from_right, R.anim.exit_to_right)
                .hide(fragment)
                .commit()
        }
    }

    fun LockFragmentSRUO(){
        if(access_token.isEmpty()){
            val frameLayout = findViewById<FrameLayout>(R.id.fl_SideRightFragmenUserOptions)
            val layoutParams2 = frameLayout.layoutParams
            layoutParams2.width = ViewGroup.LayoutParams.MATCH_PARENT
            frameLayout.layoutParams = layoutParams2
        }
    }

    @SuppressLint("Recycle")
    fun unlockFragmentSRUO() {
        if (access_token.isNotEmpty()) {

            val frameLayout = findViewById<FrameLayout>(R.id.fl_SideRightFragmenUserOptions)
            val layoutParams = frameLayout.layoutParams
            layoutParams.width = 750
            frameLayout.layoutParams = layoutParams

//                val anim = ValueAnimator.ofInt(displayWidth, 280)
//                anim.duration = 500
//                anim.addUpdateListener { valueAnimator ->
//                    val value = valueAnimator.animatedValue as Int
//                    val layoutParams = frameLayout.layoutParams
//                    layoutParams.width = value
//                    frameLayout.layoutParams = layoutParams
//                }

        }
    }

    fun loginAccount() {
        fetchAccessTokenTask.setAccessTokenListener(object : AccessTokenListener {
            override fun onAccessTokenFetched(getAccessToken: String?) {
                if (!getAccessToken.isNullOrEmpty()) {
                    access_token = getAccessToken
                    initRecyclerView()
                    getTop()
                    unlockFragmentSRUO()
                    hideFragmentSRUO()
                } else {
                    Toast.makeText(this@MainActivity, "Contraseña equivocada", Toast.LENGTH_SHORT).show()
                    fragment.binding.etSruopassword.setText("")
                }
            }
        })
        fetchAccessTokenTask.execute("4ICQyJNWimrCLb7plegtvg", "cEA_Ztz1MH99kzs8gk5iJJdG2YSQTA", fragment.loginUser, fragment.passwordUser)
    }

    private fun getDarkModeWindow(){
        val window = this.window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        window.statusBarColor = this.resources.getColor(R.color.dark_mode)
        window.navigationBarColor = this.resources.getColor(R.color.dark_mode)
    }
}