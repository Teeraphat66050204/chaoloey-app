package com.example.chaoloey

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.chaoloey.data.model.LoginResponse
import com.example.chaoloey.data.model.UpdateProfileRequest
import com.example.chaoloey.data.remote.RetrofitClient
import com.example.chaoloey.databinding.ActivityAccountSettingsBinding
import com.example.chaoloey.util.TokenManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AccountSettingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAccountSettingsBinding
    private lateinit var tokenManager: TokenManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAccountSettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        tokenManager = TokenManager(this)

        binding.nameEditText.setText(tokenManager.getUsername())

        setupClickListeners()
    }

    private fun setupClickListeners() {
        binding.backButton.setOnClickListener {
            finish()
        }

        binding.saveButton.setOnClickListener {
            val newName = binding.nameEditText.text.toString().trim()

            if (newName.isEmpty()) {
                Toast.makeText(this, getString(R.string.please_enter_name), Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (newName.length < 2) {
                Toast.makeText(this, getString(R.string.name_too_short), Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            updateProfile(newName)
        }
    }

    private fun updateProfile(newName: String) {
        binding.saveButton.isEnabled = false
        binding.saveButton.text = getString(R.string.saving)

        val token = tokenManager.getToken()
        if (token == null) {
            Toast.makeText(this, getString(R.string.auth_failed), Toast.LENGTH_SHORT).show()
            binding.saveButton.isEnabled = true
            binding.saveButton.text = getString(R.string.save_changes)
            return
        }

        val apiService = RetrofitClient.apiService
        val updateRequest = UpdateProfileRequest(name = newName)

        apiService.updateProfile("Bearer $token", updateRequest)
            .enqueue(object : Callback<LoginResponse> {
                override fun onResponse(
                    call: Call<LoginResponse>,
                    response: Response<LoginResponse>
                ) {
                    binding.saveButton.isEnabled = true
                    binding.saveButton.text = getString(R.string.save_changes)

                    if (response.isSuccessful) {
                        val loginResponse = response.body()
                        if (loginResponse?.success == true) {
                            tokenManager.saveUsername(newName)
                            Toast.makeText(this@AccountSettingsActivity, getString(R.string.profile_updated), Toast.LENGTH_SHORT).show()
                            finish()
                        } else {
                            Toast.makeText(
                                this@AccountSettingsActivity,
                                loginResponse?.message ?: getString(R.string.failed_to_update_profile),
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    } else {
                        Toast.makeText(this@AccountSettingsActivity, "Error: ${response.code()}", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                    binding.saveButton.isEnabled = true
                    binding.saveButton.text = getString(R.string.save_changes)
                    Toast.makeText(this@AccountSettingsActivity, "Network error: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
    }
}
