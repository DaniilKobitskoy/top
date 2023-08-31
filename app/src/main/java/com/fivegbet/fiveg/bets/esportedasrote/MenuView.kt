package com.fivegbet.fiveg.bets.esportedasrote

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.fivegbet.fiveg.bets.esportedasrote.databinding.ActivityMenuViewBinding

class MenuView : AppCompatActivity() {
    lateinit var binding: ActivityMenuViewBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMenuViewBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.FootballRules.setOnClickListener {
            startActivity(Intent(this, FootballRules::class.java))
        }
        binding.News.setOnClickListener {
            startActivity(Intent(this, NewsView::class.java))
        }
        binding.BasketballRules.setOnClickListener {
            startActivity(Intent(this, BasketballRules::class.java))
        }
        binding.TennisRules.setOnClickListener {
            startActivity(Intent(this, TennisRules::class.java))
        }
        binding.FootballCounter.setOnClickListener {
            startActivity(Intent(this, ScoreTimerReferee::class.java))
        }
        binding.BasketballCounter.setOnClickListener {
            startActivity(Intent(this, ReffereeScoreTimer::class.java))
        }
    }
}