package com.example.testcaseapplication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import com.example.testcaseapplication.databinding.GameActivityBinding
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfigSettings
import java.io.File

class GameActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val mainActivityBinding = GameActivityBinding.inflate(layoutInflater)

        mainActivityBinding.game21.textViewCounter = mainActivityBinding.counter
        mainActivityBinding.finishGame.setOnClickListener {
            mainActivityBinding.game21.game21.finishGame(mainActivityBinding.game21::onFinish)
        }
        setContentView(mainActivityBinding.root)
    }

}