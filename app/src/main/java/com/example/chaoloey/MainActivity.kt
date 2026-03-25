package com.example.chaoloey

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.chaoloey.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.welcomeTextView.text = getString(R.string.welcome_message)
    }
}
