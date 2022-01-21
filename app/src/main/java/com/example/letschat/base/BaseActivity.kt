package com.example.letschat.base

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.*
import com.bumptech.glide.Glide
import com.example.letschat.R
import com.example.letschat.databinding.ActivityBaseBinding
import com.example.letschat.home.ui.HomeFragment
import com.example.letschat.home.view_models.HomeViewModel
import com.example.letschat.user.User
import com.google.android.material.navigation.NavigationView
import dagger.hilt.android.AndroidEntryPoint
import de.hdodenhof.circleimageview.CircleImageView

@AndroidEntryPoint
class BaseActivity : AppCompatActivity(), View.OnClickListener {
    lateinit var drawerLayout: DrawerLayout
    lateinit var toolBar: Toolbar
    lateinit var navView: NavigationView
    lateinit var navController: NavController
    lateinit var binding: ActivityBaseBinding
    lateinit var navHeader: View

    val baseViewModel: BaseViewModel by viewModels()

    val notSignedInYetDestinations = setOf(R.id.baseFragment, R.id.loginFragment, R.id.signUpFragment)

    private companion object {
        private const val PICK_IMAGE = 500
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityBaseBinding.inflate(layoutInflater)
        val view: View = binding.getRoot()
        setContentView(view)

        init()
        setOnClickListeners()
        setOnDestinationChangedListener()

        observeUploadImage()
    }



    private fun init() {
        toolBar = findViewById(R.id.the_tool_bar)
        setSupportActionBar(toolBar)

        drawerLayout = findViewById(R.id.drawer_layout)

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as NavHostFragment
        navController = navHostFragment.navController

        navView = findViewById(R.id.nav_view)
        navView.setupWithNavController(navController)

        navHeader = binding.navView.getHeaderView(0)
    }

    private fun setOnClickListeners() {
        binding.theToolBar.toolBarIcon.setOnClickListener(this)
        binding.theToolBar.toolBarBack.setOnClickListener(this)
    }

    private fun setOnDestinationChangedListener() {
        navController.addOnDestinationChangedListener { _, des, _ ->
            when (des.id) {
                in notSignedInYetDestinations -> {
                    toolBar.visibility = View.GONE

                }
                R.id.homeFragment -> {
                    toolBar.visibility = View.VISIBLE
                    toolBar.findViewById<ImageView>(R.id.tool_bar_back).visibility = View.GONE
                    toolBar.findViewById<ImageView>(R.id.tool_bar_icon).visibility = View.VISIBLE
                    toolBar.findViewById<TextView>(R.id.tool_bar_title).text = getString(R.string.letschat)
                    toolBar.findViewById<ImageView>(R.id.tool_bar_create_new_chat).visibility = View.VISIBLE

                    // el mo4kla kda en kol ma el home fragment tegy lel forground, hro7 ageb el user info
                    getUserForDrawer()
                    observeUserForDrawer()
                }

                R.id.settingsFragment -> {
                    toolBar.visibility = View.VISIBLE
                    toolBar.findViewById<ImageView>(R.id.tool_bar_back).visibility = View.VISIBLE
                    toolBar.findViewById<ImageView>(R.id.tool_bar_icon).visibility = View.GONE
                    toolBar.findViewById<ImageView>(R.id.tool_bar_create_new_chat).visibility = View.GONE

                    toolBar.findViewById<TextView>(R.id.tool_bar_title).text = getString(R.string.settings)
                }

                else -> {
                    toolBar.visibility = View.VISIBLE
                    toolBar.findViewById<ImageView>(R.id.tool_bar_back).visibility = View.VISIBLE
                    toolBar.findViewById<ImageView>(R.id.tool_bar_create_new_chat).visibility = View.VISIBLE
                }
            }
        }
    }

    override fun onClick(view: View?) {
        when (view) {
            binding.theToolBar.toolBarIcon -> openDrawer()
            binding.theToolBar.toolBarBack -> onToolBarBackPressed()
            navHeader.findViewById<CircleImageView>(R.id.nav_header_user_image) -> {
                changeUserProfilePicture()
            }
        }
    }

    private fun onToolBarBackPressed() {
        navController.navigateUp()
    }

    private fun openDrawer() {
        binding.drawerLayout.openDrawer(GravityCompat.START)
    }

    private fun changeUserProfilePicture() {
        val imageIntent = Intent()
        imageIntent.type = "image/*"
        imageIntent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(
            Intent.createChooser(imageIntent, "Select an Image"),
            PICK_IMAGE
        );
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == BaseActivity.PICK_IMAGE && resultCode == Activity.RESULT_OK) {
            if (data == null) {
                Toast.makeText(this, "Cannot Get Image", Toast.LENGTH_SHORT).show()
            }
            val imagePath: Uri = data?.data!!
            val inputStream = contentResolver?.openInputStream(imagePath)
            val bitmap = BitmapFactory.decodeStream(inputStream)
            uploadImage(bitmap)
        }
    }

    private fun getUserForDrawer() {
        baseViewModel.getUserForDrawer()
    }

    private fun observeUserForDrawer() {
        baseViewModel.userForDrawerLiveData.observe(this, { listOfUsers ->
            listOfUsers?.let {
                showUserInfoInDrawer(it[0])
            }
        })
    }

    private fun uploadImage(bitmap: Bitmap?) {
        bitmap?.let {
            baseViewModel.uploadImage(it)
        }
    }

    private fun observeUploadImage() {
        baseViewModel.uploadImageLiveData.observe(this, { result ->
            result.getContentIfNotHandled()?.let {
                if (it.first) {
                    Toast.makeText(
                        this,
                        "Picture Uploaded Successfully",
                        Toast.LENGTH_SHORT
                    ).show()
                    navHeader.findViewById<ImageView>(R.id.nav_header_user_image).setImageBitmap(it.second)
                    binding.theToolBar.toolBarIcon.setImageBitmap(it.second)
                } else {
                    Toast.makeText(
                        this,
                        "Picture Upload Failed",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        })
    }

    private fun showUserInfoInDrawer(user: User) {
        navHeader.findViewById<TextView>(R.id.nav_header_user_name).text = user.userName
        navHeader.findViewById<TextView>(R.id.nav_header_user_email).text = user.email
        val toolBarIcon = binding.theToolBar.toolBarIcon
        Glide
            .with(this)
            .load(user.profilePictureUrl)
            .centerCrop()
            .placeholder(R.drawable.outline_account_circle_black_48)
            .into(toolBarIcon)

        val userImageView = navHeader.findViewById<ImageView>(R.id.nav_header_user_image)
        Glide
            .with(this)
            .load(user.profilePictureUrl)
            .centerCrop()
            .placeholder(R.drawable.outline_account_circle_black_48)
            .into(userImageView)

    }
}