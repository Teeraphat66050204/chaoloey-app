package com.example.chaoloey

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.chaoloey.data.model.CarAvailabilityResponse
import com.example.chaoloey.data.remote.RetrofitClient
import com.example.chaoloey.databinding.ActivityBookingDetailsBinding
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class BookingDetailsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityBookingDetailsBinding
    private var pickupDate: Calendar? = null
    private var returnDate: Calendar? = null
    private var blockedDates: Set<String> = emptySet()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBookingDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val carId = intent.getIntExtra("car_id", -1)
        if (carId != -1) loadCarAvailability(carId)

        setupLocationSpinner()
        setupClickListeners()
    }

    private fun loadCarAvailability(carId: Int) {
        RetrofitClient.apiService.getCarAvailability(carId)
            .enqueue(object : Callback<CarAvailabilityResponse> {
                override fun onResponse(call: Call<CarAvailabilityResponse>, response: Response<CarAvailabilityResponse>) {
                    if (response.isSuccessful && response.body()?.success == true) {
                        blockedDates = response.body()?.data?.blockedDates?.toSet() ?: emptySet()
                    }
                }
                override fun onFailure(call: Call<CarAvailabilityResponse>, t: Throwable) {
                    // ถ้าโหลดไม่ได้ก็ยังใช้งานได้ แค่ไม่มีการ block วัน
                }
            })
    }

    private fun setupLocationSpinner() {
        val locations = resources.getStringArray(R.array.locations)
        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            locations
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.locationSpinner.adapter = adapter
    }

    private fun setupClickListeners() {
        // Back button
        binding.backButton.setOnClickListener {
            finish()
        }

        // Rental period buttons

        // Pick up date
        binding.pickupDateEdit.setOnClickListener {
            showDateTimePicker(true)
        }

        // Return date
        binding.returnDateEdit.setOnClickListener {
            showDateTimePicker(false)
        }

        // Book now button
        binding.bookNowButton.setOnClickListener {
            if (validateForm()) {
                completeBooking()
            }
        }

        // Set today as default pickup date
        pickupDate = Calendar.getInstance()
        updatePickupDateDisplay()
    }

    private fun showDateTimePicker(isPickup: Boolean) {
        val dialog = DateTimePickerDialog(
            selectedDate = if (isPickup) pickupDate else returnDate,
            blockedDates = blockedDates,
            onDateTimeSelected = { calendar ->
                if (isPickup) {
                    pickupDate = calendar
                    updatePickupDateDisplay()
                } else {
                    returnDate = calendar
                    updateReturnDateDisplay()
                }
            }
        )
        dialog.show(supportFragmentManager, "date_time_picker")
    }

    private fun updatePickupDateDisplay() {
        pickupDate?.let {
            val format = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            binding.pickupDateEdit.setText(format.format(it.time))
        }
    }

    private fun updateReturnDateDisplay() {
        returnDate?.let {
            val format = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            binding.returnDateEdit.setText(format.format(it.time))
        }
    }

    private fun validateForm(): Boolean {
        val fullName = binding.fullNameEdit.text.toString().trim()
        val email = binding.emailEdit.text.toString().trim()
        val contact = binding.contactEdit.text.toString().trim()

        if (fullName.isEmpty()) {
            Toast.makeText(this, "Please enter full name", Toast.LENGTH_SHORT).show()
            return false
        }

        if (email.isEmpty()) {
            Toast.makeText(this, "Please enter email", Toast.LENGTH_SHORT).show()
            return false
        }

        if (contact.isEmpty()) {
            Toast.makeText(this, "Please enter contact number", Toast.LENGTH_SHORT).show()
            return false
        }

        if (returnDate == null) {
            Toast.makeText(this, "Please select return date", Toast.LENGTH_SHORT).show()
            return false
        }

        return true
    }

    private fun completeBooking() {
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val pickupStr = pickupDate?.let { dateFormat.format(it.time) } ?: ""
        val returnStr = returnDate?.let { dateFormat.format(it.time) } ?: ""

        val days = if (pickupDate != null && returnDate != null) {
            val diff = returnDate!!.timeInMillis - pickupDate!!.timeInMillis
            (diff / (1000 * 60 * 60 * 24)).toInt().coerceAtLeast(1)
        } else 1

        val carId    = intent.getIntExtra("car_id", -1)
        val carName  = intent.getStringExtra("car_name") ?: ""
        val carPrice = intent.getDoubleExtra("car_price_per_day", 0.0)
        val totalPrice = carPrice * days

        val navIntent = Intent(this, PaymentActivity::class.java)
        navIntent.putExtra("car_id", carId)
        navIntent.putExtra("car_name", carName)
        navIntent.putExtra("car_price_per_day", carPrice)
        navIntent.putExtra("pickup_date", pickupStr)
        navIntent.putExtra("return_date", returnStr)
        navIntent.putExtra("days", days)
        navIntent.putExtra("total_price", totalPrice)
        navIntent.putExtra("full_name", binding.fullNameEdit.text.toString().trim())
        navIntent.putExtra("email", binding.emailEdit.text.toString().trim())
        navIntent.putExtra("contact", binding.contactEdit.text.toString().trim())
        navIntent.putExtra("location", binding.locationSpinner.selectedItem?.toString() ?: "")
        startActivity(navIntent)
    }
}
