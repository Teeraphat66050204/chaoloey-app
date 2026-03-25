package com.example.chaoloey

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.chaoloey.data.model.BookingRequest
import com.example.chaoloey.data.model.BookingResponse
import com.example.chaoloey.data.model.ErrorResponse
import com.example.chaoloey.databinding.ActivityBookingBinding
import com.example.chaoloey.util.TokenManager
import com.example.chaoloey.data.remote.RetrofitClient
import com.google.gson.Gson
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class BookingActivity : AppCompatActivity() {

    private lateinit var binding: ActivityBookingBinding
    private lateinit var tokenManager: TokenManager

    private var carId: Int = -1
    private var startCalendar: Calendar? = null
    private var endCalendar: Calendar? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityBookingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        tokenManager = TokenManager(this)
        carId = intent.getIntExtra(EXTRA_CAR_ID, -1)

        if (carId == -1) {
            Toast.makeText(this, "Car not found", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        binding.carIdTextView.text = "Car ID: $carId"

        binding.startDateEditText.setOnClickListener {
            showDatePicker(isStartDate = true)
        }

        binding.endDateEditText.setOnClickListener {
            showDatePicker(isStartDate = false)
        }

        binding.confirmBookingButton.setOnClickListener {
            submitBooking()
        }
    }

    private fun showDatePicker(isStartDate: Boolean) {
        val calendar = Calendar.getInstance().apply {
            add(Calendar.DAY_OF_MONTH, 1)
        }

        val datePickerDialog = DatePickerDialog(
            this,
            { _, year, month, dayOfMonth ->
                val selectedDate = Calendar.getInstance().apply {
                    set(Calendar.YEAR, year)
                    set(Calendar.MONTH, month)
                    set(Calendar.DAY_OF_MONTH, dayOfMonth)
                    set(Calendar.HOUR_OF_DAY, 12)
                    set(Calendar.MINUTE, 0)
                    set(Calendar.SECOND, 0)
                    set(Calendar.MILLISECOND, 0)
                }

                val displayDate = displayDateFormat.format(selectedDate.time)

                if (isStartDate) {
                    startCalendar = selectedDate
                    binding.startDateEditText.setText(displayDate)
                } else {
                    endCalendar = selectedDate
                    binding.endDateEditText.setText(displayDate)
                }
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )

        val minDate = if (isStartDate) {
            Calendar.getInstance().apply {
                add(Calendar.DAY_OF_MONTH, 1)
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }
        } else {
            (startCalendar?.clone() as? Calendar)?.apply {
                add(Calendar.DAY_OF_MONTH, 1)
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            } ?: Calendar.getInstance().apply {
                add(Calendar.DAY_OF_MONTH, 2)
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }
        }

        datePickerDialog.datePicker.minDate = minDate.timeInMillis
        datePickerDialog.show()
    }

    private fun submitBooking() {
        val token = tokenManager.getToken()

        if (token.isNullOrEmpty()) {
            Toast.makeText(this, "Please login first", Toast.LENGTH_SHORT).show()
            return
        }

        val startDate = startCalendar
        val endDate = endCalendar

        if (startDate == null || endDate == null) {
            Toast.makeText(this, "Please select start date and end date", Toast.LENGTH_SHORT).show()
            return
        }

        if (!endDate.after(startDate)) {
            Toast.makeText(this, "End date must be later than start date", Toast.LENGTH_SHORT).show()
            return
        }

        val request = BookingRequest(
            carId = carId,
            startDate = apiDateFormat.format(startDate.time),
            endDate = apiDateFormat.format(endDate.time)
        )

        setLoading(true)

        RetrofitClient.apiService.createBooking(
            authorization = "Bearer $token",
            request = request
        ).enqueue(object : Callback<BookingResponse> {
            override fun onResponse(call: Call<BookingResponse>, response: Response<BookingResponse>) {
                setLoading(false)

                if (response.isSuccessful && response.body()?.success == true) {
                    Toast.makeText(this@BookingActivity, "Booking successful", Toast.LENGTH_SHORT).show()
                    openMyBookingsActivity()
                } else {
                    Toast.makeText(
                        this@BookingActivity,
                        parseErrorMessage(response.errorBody()),
                        Toast.LENGTH_LONG
                    ).show()
                }
            }

            override fun onFailure(call: Call<BookingResponse>, t: Throwable) {
                setLoading(false)
                Toast.makeText(
                    this@BookingActivity,
                    t.message ?: "Unable to connect to server",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }

    private fun setLoading(isLoading: Boolean) {
        binding.confirmBookingButton.isEnabled = !isLoading
        binding.confirmBookingButton.text = if (isLoading) "Booking..." else "Confirm Booking"
    }

    private fun parseErrorMessage(errorBody: ResponseBody?): String {
        return try {
            val errorResponse = Gson().fromJson(errorBody?.charStream(), ErrorResponse::class.java)
            errorResponse.message ?: "Booking failed"
        } catch (_: Exception) {
            "Booking failed"
        }
    }

    private fun openMyBookingsActivity() {
        val intent = Intent(this, MyBookingsActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        startActivity(intent)
        finish()
    }

    companion object {
        const val EXTRA_CAR_ID = "extra_car_id"

        private val displayDateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        private val apiDateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX", Locale.getDefault())
    }
}
