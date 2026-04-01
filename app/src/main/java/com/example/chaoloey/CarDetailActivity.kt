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
    private var currentCar: Car? = null

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

        binding.backButton.setOnClickListener {
            finish()
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
        currentCar = car
        binding.carNameTextView.text = car.name
        binding.brandModelTextView.text = getString(R.string.brand_model_format, car.brand, car.model)
        binding.priceTextView.text = getString(R.string.price_per_day_format, String.format(java.util.Locale.US, "%,.0f", car.pricePerDay))
        binding.seatsTextView.text = car.seats.toString()
        binding.transmissionTextView.text = car.transmission
        binding.fuelTypeTextView.text = car.fuelType
        binding.yearTextView.text = car.year.toString()
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
        val intent = Intent(this, BookingDetailsActivity::class.java)
        intent.putExtra("car_id", carId)
        currentCar?.let { car ->
            intent.putExtra("car_name", car.name)
            intent.putExtra("car_description", car.description ?: "${car.brand} ${car.model}")
            intent.putExtra("car_seats", car.seats)
            intent.putExtra("car_price_per_day", car.pricePerDay)
            intent.putExtra("car_image_url", car.imageUrl)
        }
        startActivity(intent)
    }

    companion object {
        const val EXTRA_CAR_ID = "extra_car_id"
    }
}
