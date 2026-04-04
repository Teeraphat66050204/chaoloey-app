package com.example.chaoloey

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.chaoloey.databinding.ActivityProfileBinding
import com.example.chaoloey.util.TokenManager

class ProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProfileBinding
    private lateinit var tokenManager: TokenManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        tokenManager = TokenManager(this)

        binding.usernameTextView.text = tokenManager.getUsername()

        setupClickListeners()
    }

    private fun setupClickListeners() {
        binding.logoutButton.setOnClickListener {
            logout()
        }

        binding.accountSettingsButton.setOnClickListener {
            val intent = Intent(this, AccountSettingsActivity::class.java)
            startActivity(intent)
        }

        binding.helpButton.setOnClickListener {
            Toast.makeText(this, "Help & Support coming soon", Toast.LENGTH_SHORT).show()
        }
    }

    private fun logout() {
        // Clear token
        tokenManager.clearToken()

        // Clear onboarding flag (so user sees onboarding again if desired)
        // Optional: comment this if you want to keep onboarding done
        // val sharedPref = getSharedPreferences("app_prefs", MODE_PRIVATE)
        // with(sharedPref.edit()) {
        //     putBoolean("onboarding_complete", false)
        //     apply()
        // }

        // Go to Login
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}


