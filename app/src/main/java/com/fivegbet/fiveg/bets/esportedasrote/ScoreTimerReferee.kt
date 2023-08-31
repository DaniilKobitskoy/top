package com.fivegbet.fiveg.bets.esportedasrote

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import com.fivegbet.fiveg.bets.esportedasrote.databinding.ActivityScoreTimerRefereeBinding

class ScoreTimerReferee : AppCompatActivity() {
    private var timer: CountDownTimer = object : CountDownTimer(0, 1000) {
        override fun onTick(millisUntilFinished: Long) {}
        override fun onFinish() {}
    }
    private var gameTimeInSeconds: Long = 0
    private var isTimerRunning = false
    private var homeTeamScore = 0
    private var awayTeamScore = 0
    lateinit var binding: ActivityScoreTimerRefereeBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityScoreTimerRefereeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.startStopButton.setOnClickListener {
            if (isTimerRunning) {
                stopTimer()
            } else {
                startTimer()
            }
        }

        binding.resetButton.setOnClickListener {
            resetGame()
        }

        binding.addHomeGoalButton.setOnClickListener {
            homeTeamScore++
            updateScore()
        }

        binding.addAwayGoalButton.setOnClickListener {
            awayTeamScore++
            updateScore()
        }
    }

    private fun startTimer() {
        gameTimeInSeconds = 45 * 60
        timer = object : CountDownTimer(gameTimeInSeconds * 1000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                gameTimeInSeconds = millisUntilFinished / 1000
                updateTimer()
            }

            override fun onFinish() {
                isTimerRunning = false
                updateTimer()
            }
        }
        timer.start()
        isTimerRunning = true
        updateTimer()
    }

    private fun stopTimer() {
        timer.cancel()
        isTimerRunning = false
        updateTimer()
    }

    private fun resetGame() {
        stopTimer()
        gameTimeInSeconds = 0
        homeTeamScore = 0
        awayTeamScore = 0
        updateTimer()
        updateScore()
    }

    private fun updateTimer() {
        val minutes = gameTimeInSeconds / 60
        val seconds = gameTimeInSeconds % 60
        binding.timerTextView.text = String.format("%02d:%02d", minutes, seconds)
        binding.startStopButton.text = if (isTimerRunning) "Stop" else "Start"
    }

    private fun updateScore() {
        binding.homeScoreTextView.text = homeTeamScore.toString()
        binding.awayScoreTextView.text = awayTeamScore.toString()
    }
}