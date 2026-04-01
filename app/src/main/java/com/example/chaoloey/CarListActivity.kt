package com.example.chaoloey

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.recyclerview.widget.GridLayoutManager
import com.example.chaoloey.adapter.CarAdapter
import com.example.chaoloey.data.model.Car
import com.example.chaoloey.data.model.CarFilter
import com.example.chaoloey.data.model.CarListResponse
import com.example.chaoloey.data.remote.RetrofitClient
import com.example.chaoloey.databinding.ActivityCarListBinding
import com.example.chaoloey.ui.FilterBottomSheet
import com.example.chaoloey.ui.OnboardingActivity
import com.example.chaoloey.util.TokenManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CarListActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCarListBinding
    private lateinit var carAdapter: CarAdapter
    private lateinit var recommendedCarAdapter: CarAdapter
    private lateinit var tokenManager: TokenManager

    private var allCars = mutableListOf<Car>()
    private var filteredCars = mutableListOf<Car>()
    private var currentFilter: CarFilter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        try {
            binding = ActivityCarListBinding.inflate(layoutInflater)
            setContentView(binding.root)

            tokenManager = TokenManager(this)

            if (tokenManager.getToken().isNullOrEmpty()) {
                openLoginActivity()
                return
            }

            setupRecyclerViews()
            setupClickListeners()
            setupSearchListener()
            getCars()
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    private fun setupRecyclerViews() {
        try {
            // Main cars grid (2 columns)
            carAdapter = CarAdapter(mutableListOf(), isGridLayout = true) { car ->
                openCarDetail(car)
            }
            binding.carsRecyclerView.apply {
                layoutManager = GridLayoutManager(this@CarListActivity, 2)
                adapter = carAdapter
            }

            // Recommended cars grid (2 columns)
            recommendedCarAdapter = CarAdapter(mutableListOf(), isGridLayout = true) { car ->
                openCarDetail(car)
            }
            binding.recommendedCarsRecyclerView.apply {
                layoutManager = GridLayoutManager(this@CarListActivity, 2)
                adapter = recommendedCarAdapter
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "RecyclerView Error: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }


    private fun setupClickListeners() {
        binding.filterButton.setOnClickListener {
            showFilterBottomSheet()
        }

        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    true
                }
                R.id.nav_bookings -> {
                    startActivity(Intent(this, MyBookingsActivity::class.java))
                    true
                }
                R.id.nav_profile -> {
                    startActivity(Intent(this, ProfileActivity::class.java))
                    true
                }
                else -> false
            }
        }
    }

    private fun setupSearchListener() {
        binding.searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                searchCars(s.toString())
            }
            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun searchCars(query: String) {
        val isSearching = query.isNotEmpty()

        // Hide/show recommended section during search
        binding.recommendedSectionHeader.visibility = if (isSearching) View.GONE else View.VISIBLE
        binding.recommendedCarsRecyclerView.visibility = if (isSearching) View.GONE else View.VISIBLE

        // Update "All Cars" title when searching
        binding.allCarsTitleTextView.text = if (isSearching) "Search Results" else "All Cars"

        val searchResults = if (query.isEmpty()) {
            allCars
        } else {
            allCars.filter { car ->
                car.name.contains(query, ignoreCase = true) ||
                        car.brand.contains(query, ignoreCase = true) ||
                        car.model.contains(query, ignoreCase = true)
            }.toMutableList()
        }
        carAdapter.updateData(searchResults)
        binding.emptyTextView.isVisible = searchResults.isEmpty()
    }


    private fun showFilterBottomSheet() {
        val filterBottomSheet = FilterBottomSheet { filter ->
            currentFilter = filter
            applyFilters(filter)
        }
        filterBottomSheet.show(supportFragmentManager, "FilterBottomSheet")
    }

    private fun applyFilters(filter: CarFilter) {
        val filtered = filterCars(allCars, filter)
        carAdapter.updateData(filtered)
        binding.emptyTextView.isVisible = filtered.isEmpty()
    }

    private fun filterCars(cars: List<Car>, filter: CarFilter): MutableList<Car> {
        return cars.filter { car ->
            val carTypeMatch = filter.carTypes.isEmpty() || 
                    filter.carTypes.any { it == "All Cars" || car.category.contains(it) }
            val priceMatch = filter.priceRange?.let { (min, max) ->
                car.pricePerDay.toInt() in min..max
            } ?: true
            val seatingMatch = filter.seatingCapacity.isEmpty() || 
                    filter.seatingCapacity.contains(car.seats)
            val fuelTypeMatch = filter.fuelTypes.isEmpty() || 
                    filter.fuelTypes.contains(car.fuelType)
            val locationMatch = filter.location.isNullOrEmpty() || true

            carTypeMatch && priceMatch && seatingMatch && fuelTypeMatch && locationMatch
        }.toMutableList()
    }

    private fun getCars() {
        setLoading(true)

        RetrofitClient.apiService.getCars().enqueue(object : Callback<CarListResponse> {
            override fun onResponse(call: Call<CarListResponse>, response: Response<CarListResponse>) {
                setLoading(false)

                if (response.isSuccessful && response.body()?.success == true) {
                    val cars = response.body()?.data.orEmpty()
                    allCars = cars.toMutableList()
                    filteredCars = allCars.toMutableList()

                    val recommendedCars = if (cars.size > 4) cars.subList(0, 4) else cars
                    recommendedCarAdapter.updateData(recommendedCars)
                    carAdapter.updateData(allCars)
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

    private fun openCarDetail(car: Car) {
        val intent = Intent(this, CarDetailActivity::class.java)
        intent.putExtra(CarDetailActivity.EXTRA_CAR_ID, car.id)
        startActivity(intent)
    }

    private fun setLoading(isLoading: Boolean) {
        binding.progressBar.isVisible = isLoading
    }

    private fun openLoginActivity() {
        val intent = Intent(this, OnboardingActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}
