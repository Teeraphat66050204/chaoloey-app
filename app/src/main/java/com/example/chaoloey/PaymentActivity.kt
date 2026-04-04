package com.example.chaoloey

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.chaoloey.data.model.BookingRequest
import com.example.chaoloey.data.model.BookingResponse
import com.example.chaoloey.data.remote.RetrofitClient
import com.example.chaoloey.databinding.ActivityPaymentBinding
import com.example.chaoloey.util.TokenManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.Locale

class PaymentActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPaymentBinding
    private lateinit var tokenManager: TokenManager
    private var selectedPaymentMethod = "qr" // default

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPaymentBinding.inflate(layoutInflater)
        setContentView(binding.root)

        tokenManager = TokenManager(this)

        // รับข้อมูลจาก BookingDetailsActivity
        val carName     = intent.getStringExtra("car_name") ?: ""
        val carPrice    = intent.getDoubleExtra("car_price_per_day", 0.0)
        val pickupDate  = intent.getStringExtra("pickup_date") ?: ""
        val returnDate  = intent.getStringExtra("return_date") ?: ""
        val days        = intent.getIntExtra("days", 1)
        val totalPrice  = intent.getDoubleExtra("total_price", carPrice)

        // แสดงสรุปการจอง
        binding.summaryCarName.text    = carName
        binding.summaryPickupDate.text = pickupDate
        binding.summaryReturnDate.text = returnDate
        binding.summaryDays.text       = getString(R.string.days_format, days)
        binding.summaryTotal.text      = getString(R.string.price_format, String.format(Locale.US, "%,.0f", totalPrice))

        setupClickListeners()
    }

    private fun setupClickListeners() {
        binding.backButton.setOnClickListener {
            finish()
        }

        // เลือกวิธีชำระ QR
        binding.paymentQrOption.setOnClickListener {
            selectedPaymentMethod = "qr"
            binding.radioQr.isChecked   = true
            binding.radioCard.isChecked = false
        }

        // เลือกวิธีชำระ Card
        binding.paymentCardOption.setOnClickListener {
            selectedPaymentMethod = "card"
            binding.radioQr.isChecked   = false
            binding.radioCard.isChecked = true
        }

        // ปุ่ม Pay Now → ไปหน้า Confirmation
        binding.payNowButton.setOnClickListener {
            createBooking()
        }
    }

    private fun createBooking() {
        val token = tokenManager.getToken()
        if (token.isNullOrEmpty()) {
            Toast.makeText(this, getString(R.string.auth_failed), Toast.LENGTH_SHORT).show()
            return
        }

        val carId        = intent.getIntExtra("car_id", -1)
        val pickupDateStr = intent.getStringExtra("pickup_date") ?: ""
        val returnDateStr = intent.getStringExtra("return_date") ?: ""

        if (carId == -1) {
            Toast.makeText(this, getString(R.string.car_not_found), Toast.LENGTH_SHORT).show()
            return
        }

        // แปลงวันจาก "dd/MM/yyyy" → ISO format สำหรับ API
        val displayFmt = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val apiFmt     = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX", Locale.getDefault())

        val startIso = try { apiFmt.format(displayFmt.parse(pickupDateStr)!!) } catch (_: Exception) { pickupDateStr }
        val endIso   = try { apiFmt.format(displayFmt.parse(returnDateStr)!!) } catch (_: Exception) { returnDateStr }

        setLoading(true)

        RetrofitClient.apiService.createBooking(
            authorization = "Bearer $token",
            request = BookingRequest(carId = carId, startDate = startIso, endDate = endIso)
        ).enqueue(object : Callback<BookingResponse> {
            override fun onResponse(call: Call<BookingResponse>, response: Response<BookingResponse>) {
                setLoading(false)
                if (response.isSuccessful && response.body()?.success == true) {
                    val navIntent = Intent(this@PaymentActivity, BookingConfirmationActivity::class.java)
                    navIntent.putExtras(intent)
                    navIntent.putExtra("payment_method", selectedPaymentMethod)
                    startActivity(navIntent)
                } else {
                    Toast.makeText(this@PaymentActivity, getString(R.string.booking_failed, response.code()), Toast.LENGTH_LONG).show()
                }
            }

            override fun onFailure(call: Call<BookingResponse>, t: Throwable) {
                setLoading(false)
                Toast.makeText(this@PaymentActivity, getString(R.string.network_error, t.message ?: "unknown"), Toast.LENGTH_LONG).show()
            }
        })
    }

    private fun setLoading(isLoading: Boolean) {
        binding.payNowButton.isEnabled = !isLoading
        binding.payNowButton.text = if (isLoading) getString(R.string.processing) else getString(R.string.pay_now)
    }
}
