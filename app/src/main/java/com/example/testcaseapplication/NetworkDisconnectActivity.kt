package com.example.testcaseapplication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageView

class NetworkDisconnectActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(ImageView(this).apply {
            setImageResource(R.drawable.network_disconnect)
        })
    }

    override fun onBackPressed() {
    }
}