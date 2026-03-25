package com.example.chaoloey

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.chaoloey.adapter.CarAdapter
import com.example.chaoloey.data.model.Car
import com.example.chaoloey.data.model.CarListResponse
import com.example.chaoloey.data.remote.RetrofitClient
import com.example.chaoloey.databinding.ActivityCarListBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CarListActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCarListBinding
    private lateinit var carAdapter: CarAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityCarListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()
        getCars()
    }

    private fun setupRecyclerView() {
        carAdapter = CarAdapter(mutableListOf()) { car ->
            val intent = Intent(this, CarDetailActivity::class.java)
            intent.putExtra(CarDetailActivity.EXTRA_CAR_ID, car.id)
            startActivity(intent)
        }

        binding.carsRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@CarListActivity)
            adapter = carAdapter
        }
    }

    private fun getCars() {
        setLoading(true)

        RetrofitClient.apiService.getCars().enqueue(object : Callback<CarListResponse> {
            override fun onResponse(call: Call<CarListResponse>, response: Response<CarListResponse>) {
                setLoading(false)

                if (response.isSuccessful && response.body()?.success == true) {
                    val cars = response.body()?.data.orEmpty()
                    carAdapter.updateData(cars)
                    binding.emptyTextView.isVisible = cars.isEmpty()
                } else {
                    Toast.makeText(
                        this@CarListActivity,
                        "Failed to load cars",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onFailure(call: Call<CarListResponse>, t: Throwable) {
                setLoading(false)
                Toast.makeText(
                    this@CarListActivity,
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
