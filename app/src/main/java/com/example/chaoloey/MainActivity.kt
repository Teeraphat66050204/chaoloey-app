package com.example.chaoloey

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.chaoloey.databinding.ActivityMainBinding
import com.example.chaoloey.ui.OnboardingActivity
import com.example.chaoloey.util.TokenManager

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var tokenManager: TokenManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        tokenManager = TokenManager(this)

        val intent = if (!tokenManager.getToken().isNullOrEmpty()) {
            // มี token อยู่แล้ว → ข้าม onboarding ไปหน้า Car List เลย
            Intent(this, CarListActivity::class.java)
        } else {
            // ยังไม่ได้ login → แสดง Landing Page ก่อน
            Intent(this, OnboardingActivity::class.java)
        }
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}
