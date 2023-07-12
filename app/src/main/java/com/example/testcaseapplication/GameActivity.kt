package com.example.testcaseapplication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.testcaseapplication.databinding.GameActivityBinding

class GameActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val mainActivityBinding = GameActivityBinding.inflate(layoutInflater)

        mainActivityBinding.game21.textViewCounter = mainActivityBinding.counter
        mainActivityBinding.finishGame.setOnClickListener {
            mainActivityBinding.game21.game21.finishGame(mainActivityBinding.game21::onFinishGame)
        }
        setContentView(mainActivityBinding.root)
    }

}