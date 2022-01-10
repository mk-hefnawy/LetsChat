package com.example.letschat.base

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.letschat.R
import com.example.letschat.application.MyApplication
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class BaseActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_base)
        val application = MyApplication()
    }
}