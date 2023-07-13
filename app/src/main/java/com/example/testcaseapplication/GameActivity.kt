package com.example.testcaseapplication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.testcaseapplication.databinding.GameActivityBinding
import com.example.testcaseapplication.model.Game21

class GameActivity : AppCompatActivity() {
    lateinit var game21View: Game21View
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val mainActivityBinding = GameActivityBinding.inflate(layoutInflater)
        game21View =  mainActivityBinding.game21
        game21View.game21 = if (savedInstanceState!=null){
            savedInstanceState.getSerializable("game21") as Game21
        } else{
            Game21()
        }
        game21View.textViewCounter = mainActivityBinding.counter
        mainActivityBinding.finishGame.setOnClickListener {
            game21View.game21.finishGame(game21View::onFinishGame)
        }
        setContentView(mainActivityBinding.root)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putSerializable("game21",game21View.game21)
        super.onSaveInstanceState(outState)
    }
}