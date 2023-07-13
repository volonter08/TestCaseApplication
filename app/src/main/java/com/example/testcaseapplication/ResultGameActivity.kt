package com.example.testcaseapplication

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.bumptech.glide.Glide
import com.example.testcaseapplication.databinding.ActivityResultGameBinding

class ResultGameActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val isWin = intent.getBooleanExtra("is_win",false)
        val myCount = intent.getIntExtra("my_count",0)
        val countOpponent = intent.getIntExtra("count_opponent",0)
        val resultGameActivityBinding = ActivityResultGameBinding.inflate(layoutInflater)
        Glide.with(this).load(if(isWin) R.drawable.win_giphy else R.drawable.loss_giphy).into(resultGameActivityBinding.imageview)
        resultGameActivityBinding.textview.text = getString(if(isWin) R.string.congratulation else R.string.loss)+ " Your points: $myCount. Opponent's points: $countOpponent"
        setContentView(resultGameActivityBinding.root)
    }
}