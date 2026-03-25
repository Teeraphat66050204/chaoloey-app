package com.example.chaoloey

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.chaoloey.adapter.BookingAdapter
import com.example.chaoloey.data.model.BookingListResponse
import com.example.chaoloey.data.remote.RetrofitClient
import com.example.chaoloey.databinding.ActivityMyBookingsBinding
import com.example.chaoloey.util.TokenManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MyBookingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMyBookingsBinding
    private lateinit var tokenManager: TokenManager
    private lateinit var bookingAdapter: BookingAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMyBookingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        tokenManager = TokenManager(this)

        setupRecyclerView()
        loadBookings()
    }

    private fun setupRecyclerView() {
        bookingAdapter = BookingAdapter(mutableListOf())

        binding.bookingsRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@MyBookingsActivity)
            adapter = bookingAdapter
        }
    }

    private fun loadBookings() {
        val token = tokenManager.getToken()

        if (token.isNullOrEmpty()) {
            Toast.makeText(this, "Please login first", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        setLoading(true)

        RetrofitClient.apiService.getMyBookings("Bearer $token")
            .enqueue(object : Callback<BookingListResponse> {
                override fun onResponse(
                    call: Call<BookingListResponse>,
                    response: Response<BookingListResponse>
                ) {
                    setLoading(false)

                    if (response.isSuccessful && response.body()?.success == true) {
                        val bookings = response.body()?.data.orEmpty()
                        bookingAdapter.updateData(bookings)
                        binding.emptyTextView.isVisible = bookings.isEmpty()
                    } else {
                        Toast.makeText(
                            this@MyBookingsActivity,
                            "Failed to load bookings",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onFailure(call: Call<BookingListResponse>, t: Throwable) {
                    setLoading(false)
                    Toast.makeText(
                        this@MyBookingsActivity,
                        t.message ?: "Unable to connect to server",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
    }

    private fun setLoading(isLoading: Boolean) {
        binding.progressBar.isVisible = isLoading
    }
}
