package com.example.letschat.home.ui

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.example.letschat.R
import com.example.letschat.chatroom.chat.ChatRoom
import com.example.letschat.databinding.FragmentHomeBinding
import com.example.letschat.di.AppContainer
import com.example.letschat.home.adapters.FragmentsAdapter
import com.example.letschat.home.view_models.HomeViewModel
import com.example.letschat.user.User
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : Fragment(), View.OnClickListener{

    private lateinit var homeFragmentBinding: FragmentHomeBinding
    val homeViewModel: HomeViewModel by viewModels()
    private lateinit var appContainer: AppContainer
    private lateinit var navController: NavController

    private lateinit var fragmentsAdapter: FragmentsAdapter
    private var hasFragmentBeenVisible = false

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
        if (!hasFragmentBeenVisible){
            syncCacheWithServer()
        }else{
            hasFragmentBeenVisible = true
            homeFragmentBinding.homeFragmentProgressBar.visibility = View.GONE
            homeFragmentBinding.homeFragmentContent.visibility = View.VISIBLE
        }

        setOnClickListeners()
        setUpTabLayout()

        homeFragmentBinding.testButton.setOnClickListener{
            signOut(appContainer.auth)
        }
    }
    private fun setUp() {
       homeFragmentBinding = DataBindingUtil.bind(view?.findViewById(R.id.home_fragment_root)!!)!!

        appContainer = AppContainer(requireContext())
        homeFragmentBinding.viewModel = homeViewModel
        homeFragmentBinding.lifecycleOwner = this
        navController = findNavController()
    }
    private fun syncCacheWithServer() {
        Log.d("Here", "syncCacheWithServer")
        homeViewModel.getUserDataFromServer()
        homeViewModel.userDataFromServerLiveData.observe(viewLifecycleOwner){ event->
            event.getContentIfNotHandled()?.let { userInServer->
                updateUserInCacheWithServerData(userInServer)
            }
        }
    }

    private fun syncChats(){
        homeViewModel.getUserChatsFromBothServerAndCache()
        homeViewModel.userChatsInServerLiveData.observe(viewLifecycleOwner){ userChatsInServerEvent->
            userChatsInServerEvent.getContentIfNotHandled()?.let {
                updateUserChatsInCache(it)
            }
        }
    }

    private fun updateUserChatsInCache(userChatsInServer: List<ChatRoom>?) {
        homeViewModel.updateUserChatsInCache(userChatsInServer)
        homeViewModel.updateUserChatsInCacheLiveData.observe(viewLifecycleOwner){ event->
            event.getContentIfNotHandled()?.let { res->
                if (!res.contains(-1L)){
                    homeFragmentBinding.homeFragmentProgressBar.visibility = View.GONE
                    homeFragmentBinding.homeFragmentContent.visibility = View.VISIBLE
                    Toast.makeText(requireContext(), "Sync is Successful", Toast.LENGTH_SHORT).show()

                }else{
                    homeFragmentBinding.homeFragmentProgressBar.visibility = View.GONE
                    homeFragmentBinding.homeFragmentContent.visibility = View.GONE
                    homeFragmentBinding.syncFailed.visibility = View.VISIBLE
                    Log.d("Here", "Sync Failed")
                }
            }
        }
    }

    private fun updateUserInCacheWithServerData(serverData: User?) {
        homeViewModel.updateUserInCacheWithServerData(serverData)
        homeViewModel.updateUserDataInCacheResultLiveData.observe(viewLifecycleOwner){
            it.getContentIfNotHandled()?.let { res->
                if (res != -1L){
                    syncChats()
                }else{
                    homeFragmentBinding.homeFragmentProgressBar.visibility = View.GONE
                    homeFragmentBinding.homeFragmentContent.visibility = View.GONE
                    homeFragmentBinding.syncFailed.visibility = View.VISIBLE
                    Log.d("Here", "Sync Failed")
                }
            }
        }
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