package com.example.chaoloey

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.example.chaoloey.data.model.ErrorResponse
import com.example.chaoloey.data.model.LoginResponse
import com.example.chaoloey.data.model.RegisterRequest
import com.example.chaoloey.data.remote.RetrofitClient
import com.example.chaoloey.databinding.ActivityRegisterBinding
import com.example.chaoloey.util.TokenManager
import com.google.gson.Gson
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private lateinit var tokenManager: TokenManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        tokenManager = TokenManager(this)

        binding.registerButton.setOnClickListener {
            register()
        }

        binding.goToLoginTextView.setOnClickListener {
            finish()
        }
    }

    private fun register() {
        val name = binding.nameEditText.text?.toString()?.trim().orEmpty()
        val email = binding.emailEditText.text?.toString()?.trim().orEmpty()
        val password = binding.passwordEditText.text?.toString()?.trim().orEmpty()

        if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, getString(R.string.register_empty_fields), Toast.LENGTH_SHORT).show()
            return
        }

        setLoading(true)

        val request = RegisterRequest(name = name, email = email, password = password)

        RetrofitClient.apiService.register(request).enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                setLoading(false)

                if (response.isSuccessful) {
                    val registerResponse = response.body()
                    val token = registerResponse?.data?.token

                    if (registerResponse?.success == true && !token.isNullOrEmpty()) {
                        tokenManager.saveToken(token)
                        Toast.makeText(
                            this@RegisterActivity,
                            getString(R.string.register_success),
                            Toast.LENGTH_SHORT
                        ).show()
                        openCarListActivity()
                    } else {
                        Toast.makeText(
                            this@RegisterActivity,
                            getString(R.string.register_failed),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } else {
                    Toast.makeText(
                        this@RegisterActivity,
                        parseErrorMessage(response.errorBody()),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                setLoading(false)
                Toast.makeText(
                    this@RegisterActivity,
                    getString(R.string.network_error, t.message ?: "unknown error"),
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }

    private fun parseErrorMessage(errorBody: ResponseBody?): String {
        return try {
            val errorResponse = Gson().fromJson(errorBody?.charStream(), ErrorResponse::class.java)
            errorResponse.message ?: getString(R.string.register_failed)
        } catch (_: Exception) {
            getString(R.string.register_failed)
        }
    }

    private fun setLoading(isLoading: Boolean) {
        binding.progressBar.isVisible = isLoading
        binding.registerButton.isEnabled = !isLoading
        binding.nameEditText.isEnabled = !isLoading
        binding.emailEditText.isEnabled = !isLoading
        binding.passwordEditText.isEnabled = !isLoading
        binding.nameEditText.alpha = if (isLoading) 0.5f else 1.0f
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
