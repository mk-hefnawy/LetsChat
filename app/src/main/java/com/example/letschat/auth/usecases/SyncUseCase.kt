package com.example.letschat.auth.usecases

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LifecycleOwner
import androidx.navigation.NavController
import com.example.letschat.auth.ui.LoginFragmentDirections
import com.example.letschat.auth.ui.SignUpFragmentDirections
import com.example.letschat.chatroom.chat.ChatRoom
import com.example.letschat.home.view_models.HomeViewModel
import com.example.letschat.other.SIGNUP
import com.example.letschat.user.User

class SyncUseCase(
    val context: Context,
    val viewLifecycleOwner: LifecycleOwner,
    val homeViewModel: HomeViewModel,
    val navController: NavController,
    val whichFragment: Int
) {
    init {
        syncCacheWithServer()
    }

    private fun syncCacheWithServer() {
        Log.d("Here", "syncCacheWithServer")
        homeViewModel.getUserDataFromServer()
        homeViewModel.userDataFromServerLiveData.observe(viewLifecycleOwner) { event ->
            event.getContentIfNotHandled()?.let { userInServer ->
                updateUserInCacheWithServerData(userInServer)
            }
        }
    }

    private fun updateUserInCacheWithServerData(serverData: User?) {
        homeViewModel.updateUserInCacheWithServerData(serverData)
        homeViewModel.updateUserDataInCacheResultLiveData.observe(viewLifecycleOwner) {
            it.getContentIfNotHandled()?.let { res ->
                if (res != -1L) {
                    syncChats()
                } else {
                    Log.d("Here", "Sync Failed")
                }
            }
        }
    }

    private fun syncChats() {
        homeViewModel.getUserChatsFromBothServerAndCache()
        homeViewModel.userChatsInServerLiveData.observe(viewLifecycleOwner) { userChatsInServerEvent ->
            userChatsInServerEvent.getContentIfNotHandled()?.let {
                updateUserChatsInCache(it)
            }
        }
    }

    private fun updateUserChatsInCache(userChatsInServer: List<ChatRoom>?) {
        homeViewModel.updateUserChatsInCache(userChatsInServer)
        homeViewModel.updateUserChatsInCacheLiveData.observe(viewLifecycleOwner) { event ->
            event.getContentIfNotHandled()?.let { res ->
                if (!res.contains(-1L)) {
                    goToHomeFragment()
                    Toast.makeText(context, "Sync is Successful", Toast.LENGTH_SHORT)
                        .show()

                } else {
                    Log.d("Here", "Sync Failed")
                }
            }
        }
    }

    private fun goToHomeFragment() {
        if (whichFragment == SIGNUP){
            navController.navigate(SignUpFragmentDirections.actionSignUpToHome())
        }else{
            navController.navigate(LoginFragmentDirections.actionLoginToHome())
        }

    }

}