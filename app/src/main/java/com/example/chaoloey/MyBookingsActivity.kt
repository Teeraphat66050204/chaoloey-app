package com.example.chaoloey

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.chaoloey.adapter.BookingAdapter
import com.example.chaoloey.data.model.BookingItem
import com.example.chaoloey.data.model.BookingListResponse
import com.example.chaoloey.data.remote.RetrofitClient
import com.example.chaoloey.databinding.ActivityMyBookingsBinding
import com.example.chaoloey.util.TokenManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MyBookingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMyBookingsBinding
    private lateinit var bookingAdapter: BookingAdapter
    private lateinit var tokenManager: TokenManager
    private val bookingsList = mutableListOf<BookingItem>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMyBookingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        tokenManager = TokenManager(this)
        setupRecyclerView()
        setupActions()
        loadBookings()
    }

    private fun setupRecyclerView() {
        bookingAdapter = BookingAdapter(bookingsList) { booking ->
            val navIntent = Intent(this@MyBookingsActivity, MyBookingDetailActivity::class.java)
            navIntent.putExtra("booking_id",  booking.id)
            navIntent.putExtra("car_name",    booking.car.name)
            navIntent.putExtra("car_image",   booking.car.imageUrl)
            navIntent.putExtra("start_date",  booking.startDate)
            navIntent.putExtra("end_date",    booking.endDate)
            navIntent.putExtra("total_price", booking.totalPrice)
            navIntent.putExtra("status",      booking.status)
            startActivity(navIntent)
        }
        binding.bookingsRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@MyBookingsActivity)
            adapter = bookingAdapter
        }
    }

    private fun setupActions() {
        binding.backButton.setOnClickListener { finish() }
    }

    private fun loadBookings() {
        val token = tokenManager.getToken()
        if (token == null) {
            showEmpty()
            return
        }

        showLoading(true)

        RetrofitClient.apiService.getMyBookings("Bearer $token")
            .enqueue(object : Callback<BookingListResponse> {
                override fun onResponse(call: Call<BookingListResponse>, response: Response<BookingListResponse>) {
                    showLoading(false)
                    if (response.isSuccessful && response.body()?.success == true) {
                        bookingsList.clear()
                        bookingsList.addAll(response.body()?.data ?: emptyList())
                    } else {
                        Toast.makeText(this@MyBookingsActivity, "โหลดข้อมูลไม่สำเร็จ", Toast.LENGTH_SHORT).show()
                    }
                    updateUI()
                }

                override fun onFailure(call: Call<BookingListResponse>, t: Throwable) {
                    showLoading(false)
                    Toast.makeText(this@MyBookingsActivity, "ไม่สามารถเชื่อมต่อได้: ${t.message}", Toast.LENGTH_SHORT).show()
                    updateUI()
                }
            })
    }

    private fun showLoading(show: Boolean) {
        binding.progressBar.visibility        = if (show) View.VISIBLE else View.GONE
        binding.bookingsRecyclerView.visibility = if (show) View.GONE  else View.VISIBLE
        if (show) binding.emptyStateContainer.visibility = View.GONE
    }

    private fun showEmpty() {
        binding.progressBar.visibility          = View.GONE
        binding.bookingsRecyclerView.visibility = View.GONE
        binding.emptyStateContainer.visibility  = View.VISIBLE
    }

    private fun updateUI() {
        if (bookingsList.isEmpty()) {
            showEmpty()
        } else {
            binding.emptyStateContainer.visibility  = View.GONE
            binding.bookingsRecyclerView.visibility = View.VISIBLE
            bookingAdapter.notifyItemRangeInserted(0, bookingsList.size)
        }
    }
}
