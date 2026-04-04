package com.example.chaoloey

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.chaoloey.data.model.ErrorResponse
import com.example.chaoloey.data.model.LoginRequest
import com.example.chaoloey.data.model.LoginResponse
import com.example.chaoloey.data.remote.RetrofitClient
import com.example.chaoloey.databinding.ActivityLoginBinding
import com.example.chaoloey.util.TokenManager
import com.google.gson.Gson
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var tokenManager: TokenManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        try {
            binding = ActivityLoginBinding.inflate(layoutInflater)
            setContentView(binding.root)

            tokenManager = TokenManager(this)

            // Check if already logged in
            if (!tokenManager.getToken().isNullOrEmpty()) {
                openCarListActivity()
                return
            }

            setupClickListeners()
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    private fun setupClickListeners() {
        // Login button click
        binding.loginButton.setOnClickListener {
            login()
        }

        // Go to register / Sign up
        binding.signUpTextView.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }

    private fun login() {
        val email = binding.emailEditText.text?.toString()?.trim().orEmpty()
        val password = binding.passwordEditText.text?.toString()?.trim().orEmpty()

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, getString(R.string.login_empty_fields), Toast.LENGTH_SHORT).show()
            return
        }

        setLoading(true)

        val request = LoginRequest(email = email, password = password)

        RetrofitClient.apiService.login(request).enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                setLoading(false)

                if (response.isSuccessful) {
                    val loginResponse = response.body()
                    val token = loginResponse?.data?.token

                    if (loginResponse?.success == true && !token.isNullOrEmpty()) {
                        tokenManager.saveToken(token)
                        val userName = loginResponse.data?.user?.name ?: ""
                        if (userName.isNotEmpty()) {
                            tokenManager.saveUsername(userName)
                        }

                        Toast.makeText(
                            this@LoginActivity,
                            getString(R.string.login_success),
                            Toast.LENGTH_SHORT
                        ).show()
                        openCarListActivity()
                    } else {
                        Toast.makeText(
                            this@LoginActivity,
                            getString(R.string.login_failed),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } else {
                    Toast.makeText(
                        this@LoginActivity,
                        parseErrorMessage(response.errorBody()),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                setLoading(false)
                Toast.makeText(
                    this@LoginActivity,
                    getString(R.string.network_error, t.message ?: "unknown error"),
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }

    private fun parseErrorMessage(errorBody: ResponseBody?): String {
        return try {
            val errorResponse = Gson().fromJson(errorBody?.charStream(), ErrorResponse::class.java)
            errorResponse.message ?: getString(R.string.login_failed)
        } catch (_: Exception) {
            getString(R.string.login_failed)
        }
    }

    private fun setLoading(isLoading: Boolean) {
        binding.loginButton.isEnabled = !isLoading
        binding.emailEditText.isEnabled = !isLoading
        binding.passwordEditText.isEnabled = !isLoading
        binding.emailEditText.alpha = if (isLoading) 0.5f else 1.0f
        binding.passwordEditText.alpha = if (isLoading) 0.5f else 1.0f
    }

    private fun openCarListActivity() {
        val intent = Intent(this, CarListActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}

