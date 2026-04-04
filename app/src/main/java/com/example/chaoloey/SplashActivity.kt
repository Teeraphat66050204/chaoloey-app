package com.example.chaoloey

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.example.chaoloey.ui.OnboardingActivity
import com.example.chaoloey.util.TokenManager

class SplashActivity : AppCompatActivity() {

    private lateinit var tokenManager: TokenManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        tokenManager = TokenManager(this)

        // Delay 2 seconds then navigate to appropriate screen
        Handler(Looper.getMainLooper()).postDelayed({
            navigateToNextScreen()
        }, 2000)
    }

    private fun navigateToNextScreen() {
        val intent = if (!tokenManager.getToken().isNullOrEmpty()) {
            Intent(this, CarListActivity::class.java)
        } else {
            Intent(this, OnboardingActivity::class.java)
        }
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}
