package com.example.chaoloey

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import com.example.chaoloey.data.model.Car
import com.example.chaoloey.data.model.CarDetailResponse
import com.example.chaoloey.data.remote.RetrofitClient
import com.example.chaoloey.databinding.ActivityCarDetailBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CarDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCarDetailBinding
    private var carId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityCarDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        carId = intent.getIntExtra(EXTRA_CAR_ID, -1)

        if (carId == -1) {
            Toast.makeText(this, "Car not found", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        binding.bookNowButton.setOnClickListener {
            openBookingActivity()
        }

        loadCarDetail()
    }

    private fun loadCarDetail() {
        setLoading(true)

        RetrofitClient.apiService.getCarById(carId).enqueue(object : Callback<CarDetailResponse> {
            override fun onResponse(call: Call<CarDetailResponse>, response: Response<CarDetailResponse>) {
                setLoading(false)

                if (response.isSuccessful && response.body()?.success == true) {
                    val car = response.body()?.data
                    if (car != null) {
                        showCarDetail(car)
                    } else {
                        Toast.makeText(this@CarDetailActivity, "Car not found", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this@CarDetailActivity, "Failed to load car detail", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<CarDetailResponse>, t: Throwable) {
                setLoading(false)
                Toast.makeText(
                    this@CarDetailActivity,
                    t.message ?: "Unable to connect to server",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }

    private fun showCarDetail(car: Car) {
        binding.carNameTextView.text = car.name
        binding.brandModelTextView.text = "${car.brand} ${car.model}"
        binding.priceTextView.text = "THB ${car.pricePerDay.toInt()} / day"
        binding.descriptionTextView.text = car.description ?: "No description available"

        Glide.with(this)
            .load(car.imageUrl)
            .centerCrop()
            .into(binding.carImageView)
    }

    private fun setLoading(isLoading: Boolean) {
        binding.progressBar.isVisible = isLoading
        binding.contentGroup.isVisible = !isLoading
    }

    private fun openBookingActivity() {
        val intent = Intent(this, BookingActivity::class.java)
        intent.putExtra(BookingActivity.EXTRA_CAR_ID, carId)
        startActivity(intent)
    }

    companion object {
        const val EXTRA_CAR_ID = "extra_car_id"
    }
}
