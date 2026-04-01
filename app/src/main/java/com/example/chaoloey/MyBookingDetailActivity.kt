package com.example.chaoloey

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.chaoloey.databinding.ActivityMyBookingDetailBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit
import kotlin.math.abs

class MyBookingDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMyBookingDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMyBookingDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val bookingId  = intent.getIntExtra("booking_id", 0)
        val carName    = intent.getStringExtra("car_name") ?: ""
        val carImage   = intent.getStringExtra("car_image") ?: ""
        val startDate  = intent.getStringExtra("start_date") ?: ""
        val endDate    = intent.getStringExtra("end_date") ?: ""
        val totalPrice = intent.getDoubleExtra("total_price", 0.0)
        val status     = intent.getStringExtra("status") ?: ""

        binding.carNameTextView.text  = carName
        binding.bookingIdTextView.text = getString(R.string.booking_id_format, bookingId)
        binding.totalPriceTextView.text = getString(R.string.price_format, String.format(Locale.US, "%,.0f", totalPrice))

        // แปลงวันที่
        val inputFmt  = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
        val outputFmt = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
        val startParsed: Date? = try { inputFmt.parse(startDate) } catch (_: Exception) { null }
        val endParsed: Date?   = try { inputFmt.parse(endDate) }   catch (_: Exception) { null }

        binding.startDateTextView.text = startParsed?.let { outputFmt.format(it) } ?: startDate
        binding.endDateTextView.text   = endParsed?.let { outputFmt.format(it) }   ?: endDate

        // คำนวณจำนวนวัน
        if (startParsed != null && endParsed != null) {
            val diffMs   = abs(endParsed.time - startParsed.time)
            val days     = TimeUnit.MILLISECONDS.toDays(diffMs).toInt().coerceAtLeast(1)
            binding.durationTextView.text = getString(R.string.days_format, days)
        } else {
            binding.durationTextView.text = "-"
        }

        // Status chip
        binding.statusChip.text = status
        binding.statusChip.setChipBackgroundColorResource(
            when (status.lowercase()) {
                "confirmed" -> R.color.booking_confirmed_bg
                "pending"   -> R.color.booking_pending_bg
                "completed" -> R.color.booking_completed_bg
                else        -> R.color.booking_pending_bg
            }
        )

        // Car image
        if (carImage.isNotEmpty()) {
            Glide.with(this)
                .load(carImage)
                .centerCrop()
                .placeholder(R.color.login_input_background)
                .into(binding.carImageView)
        }

        binding.backButton.setOnClickListener { finish() }
    }
}
