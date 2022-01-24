package com.example.letschat.home.ui

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.ImageView
import androidx.fragment.app.Fragment
import android.widget.TextView
import android.widget.Toast
import android.widget.Toolbar
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.GravityCompat
import androidx.databinding.DataBindingUtil
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import com.bumptech.glide.Glide
import com.example.letschat.R
import com.example.letschat.databinding.FragmentHomeBinding
import com.example.letschat.di.AppContainer
import com.example.letschat.home.adapters.FragmentsAdapter
import com.example.letschat.home.view_models.HomeViewModel
import com.example.letschat.user.User
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.material.navigation.NavigationView
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.auth.FirebaseAuth

class HomeFragment : Fragment(), View.OnClickListener{

    private lateinit var homeFragmentBinding: FragmentHomeBinding
    private lateinit var homeViewModel: HomeViewModel
    private lateinit var appContainer: AppContainer
    private lateinit var navController: NavController

    private lateinit var fragmentsAdapter: FragmentsAdapter
    private lateinit var header: View

    private companion object {
        private const val TAG = "HomeFragmentHere"
        private const val GOOGLE_SIGN_IN_REQUEST_CODE = 300

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUp()
        setOnClickListeners()
        setUpTabLayout()

        homeFragmentBinding.testButton.setOnClickListener{
            signOut(appContainer.auth)
        }
    }

    private fun setUp() {
        homeFragmentBinding = DataBindingUtil.bind(view?.findViewById(R.id.home_fragment_root)!!)!!


        appContainer = AppContainer(requireContext())
        homeViewModel = appContainer.homeViewModel
        homeFragmentBinding.viewModel = homeViewModel
        homeFragmentBinding.lifecycleOwner = this
        navController = findNavController()
    }

    private fun setUpTabLayout() {
        fragmentsAdapter = FragmentsAdapter(childFragmentManager, lifecycle)

        fragmentsAdapter.addFragment(ChatsFragment())
        fragmentsAdapter.addFragment(FriendsFragment())
        fragmentsAdapter.addFragment(FriendRequestsFragment())


        homeFragmentBinding.viewPager.adapter = fragmentsAdapter
        TabLayoutMediator(
            homeFragmentBinding.tabLayout,
            homeFragmentBinding.viewPager
        ) { tab, pos ->
            when (pos) {
                0 -> tab.text = activity?.getString(R.string.chats) // to support multi languages
                1 -> tab.text = activity?.getString(R.string.friends)
                2 -> tab.text = activity?.getString(R.string.friend_requests)
            }
        }.attach()

        homeFragmentBinding.tabLayout.addOnTabSelectedListener(object :
            TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                homeFragmentBinding.viewPager.setCurrentItem(tab?.position!!, false)
            }
            override fun onTabUnselected(tab: TabLayout.Tab?) {
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
            }
        })
    }

    private fun getUserInfo(docId: String) {
        homeViewModel.getUserInfo(docId)
        homeViewModel.userInfoLiveData.observe(viewLifecycleOwner, {
        })
    }

    private fun setOnClickListeners() {
        homeFragmentBinding.addFriendButton.setOnClickListener(this)
    }


    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.add_friend_button -> onAddFriendButtonClicked()
        }
    }

    private fun onAddFriendButtonClicked() {
        navController.navigate(HomeFragmentDirections.actionHomeToAddFriend())
    }

    private fun signOut(auth: FirebaseAuth) {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        val signInGoogleUser = GoogleSignIn.getClient(requireActivity(), gso)

        auth.signOut()
        Log.d(TAG, "after auth.signOut()")
        signInGoogleUser.signOut()
        Log.d(TAG, "after signInGoogleUser.signOut()")
        navController.navigate(HomeFragmentDirections.actionHomeFragmentToLoginFragment())
    }
}