package com.example.chaoloey

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.chaoloey.databinding.ActivityBookingConfirmationBinding
import java.util.Locale

class BookingConfirmationActivity : AppCompatActivity() {

    private lateinit var binding: ActivityBookingConfirmationBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBookingConfirmationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val carName    = intent.getStringExtra("car_name") ?: ""
        val pickupDate = intent.getStringExtra("pickup_date") ?: ""
        val returnDate = intent.getStringExtra("return_date") ?: ""
        val totalPrice = intent.getDoubleExtra("total_price", 0.0)

        binding.confirmCarName.text    = carName
        binding.confirmPickupDate.text = pickupDate
        binding.confirmReturnDate.text = returnDate
        binding.confirmTotal.text      = getString(R.string.price_format, String.format(Locale.US, "%,.0f", totalPrice))

        binding.backToHomeButton.setOnClickListener {
            val navIntent = Intent(this, CarListActivity::class.java)
            navIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            startActivity(navIntent)
            finish()
        }
    }
}
