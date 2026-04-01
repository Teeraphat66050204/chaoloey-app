package com.example.chaoloey.ui

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.LinearLayout
import com.example.chaoloey.data.model.CarFilter
import com.example.chaoloey.databinding.BottomSheetFilterBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.slider.RangeSlider
import java.util.Calendar

class FilterBottomSheet(
    private val onFilterApplied: (CarFilter) -> Unit
) : BottomSheetDialogFragment() {

    private lateinit var binding: BottomSheetFilterBinding
    private val selectedCarTypes = mutableSetOf<String>()
    private val selectedColors = mutableSetOf<String>()
    private val selectedSeatingCapacity = mutableSetOf<Int>()
    private val selectedFuelTypes = mutableSetOf<String>()
    private var minPrice = 0
    private var maxPrice = 500
    private var selectedRentalTime: String? = null
    private var pickupDate: String? = null
    private var dropoffDate: String? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = BottomSheetFilterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupCarTypes()
        setupPriceRange()
        setupRentalTime()
        setupDatePickers()
        setupColors()
        setupSeatingCapacity()
        setupFuelTypes()
        setupButtons()
    }

    private fun setupCarTypes() {
        val carTypes = listOf("All Cars", "Regular Cars", "Luxury Cars")
        carTypes.forEach { type ->
            val checkBox = CheckBox(requireContext()).apply {
                text = type
                setOnCheckedChangeListener { _, isChecked ->
                    if (isChecked) selectedCarTypes.add(type)
                    else selectedCarTypes.remove(type)
                }
            }
            binding.carTypesContainer.addView(checkBox)
        }
    }

    private fun setupPriceRange() {
        binding.priceRangeSlider.addOnChangeListener { slider, _, _ ->
            val values = slider.values
            minPrice = values[0].toInt()
            maxPrice = values[1].toInt()
            binding.minPriceTextView.text = "$$minPrice"
            binding.maxPriceTextView.text = "$$maxPrice"
        }
    }

    private fun setupRentalTime() {
        val rentalTimes = listOf("Hour", "Day", "Weekly", "Monthly")
        rentalTimes.forEach { time ->
            val button = Button(requireContext()).apply {
                text = time
                isSelected = false
                setOnClickListener {
                    rentalTimes.forEach { t ->
                        binding.rentalTimeContainer.findViewById<Button>(
                            android.R.id.button1 + rentalTimes.indexOf(t)
                        )?.isSelected = false
                    }
                    isSelected = true
                    selectedRentalTime = time
                }
            }
            binding.rentalTimeContainer.addView(button)
        }
    }

    private fun setupDatePickers() {
        binding.pickupDateButton.setOnClickListener {
            showDatePicker { date ->
                pickupDate = date
                binding.pickupDateButton.text = "Pickup: $date"
            }
        }

        binding.dropoffDateButton.setOnClickListener {
            showDatePicker { date ->
                dropoffDate = date
                binding.dropoffDateButton.text = "Dropoff: $date"
            }
        }
    }

    private fun showDatePicker(onDateSelected: (String) -> Unit) {
        val calendar = Calendar.getInstance()
        DatePickerDialog(
            requireContext(),
            { _, year, month, dayOfMonth ->
                val date = String.format("%02d/%02d/%d", dayOfMonth, month + 1, year)
                onDateSelected(date)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    private fun setupColors() {
        val colors = listOf("White", "Gray", "Blue", "Black")
        colors.forEach { color ->
            val checkBox = CheckBox(requireContext()).apply {
                text = color
                setOnCheckedChangeListener { _, isChecked ->
                    if (isChecked) selectedColors.add(color)
                    else selectedColors.remove(color)
                }
            }
            binding.colorsContainer.addView(checkBox)
        }
    }

    private fun setupSeatingCapacity() {
        val capacities = listOf(2, 4, 5, 8)
        capacities.forEach { capacity ->
            val checkBox = CheckBox(requireContext()).apply {
                text = "$capacity Seats"
                setOnCheckedChangeListener { _, isChecked ->
                    if (isChecked) selectedSeatingCapacity.add(capacity)
                    else selectedSeatingCapacity.remove(capacity)
                }
            }
            binding.seatingCapacityContainer.addView(checkBox)
        }
    }

    private fun setupFuelTypes() {
        val fuelTypes = listOf("Electric", "Petrol", "Diesel", "Hybrid")
        fuelTypes.forEach { fuel ->
            val checkBox = CheckBox(requireContext()).apply {
                text = fuel
                setOnCheckedChangeListener { _, isChecked ->
                    if (isChecked) selectedFuelTypes.add(fuel)
                    else selectedFuelTypes.remove(fuel)
                }
            }
            binding.fuelTypeContainer.addView(checkBox)
        }
    }

    private fun setupButtons() {
        binding.clearAllButton.setOnClickListener {
            clearAllFilters()
        }

        binding.showCarsButton.setOnClickListener {
            applyFilters()
        }
    }

    private fun clearAllFilters() {
        selectedCarTypes.clear()
        selectedColors.clear()
        selectedSeatingCapacity.clear()
        selectedFuelTypes.clear()
        minPrice = 0
        maxPrice = 500
        selectedRentalTime = null
        pickupDate = null
        dropoffDate = null
        binding.locationEditText.text.clear()

        // Reset UI
        binding.carTypesContainer.let { container ->
            for (i in 0 until container.childCount) {
                (container.getChildAt(i) as? CheckBox)?.isChecked = false
            }
        }
        binding.colorsContainer.let { container ->
            for (i in 0 until container.childCount) {
                (container.getChildAt(i) as? CheckBox)?.isChecked = false
            }
        }
        binding.seatingCapacityContainer.let { container ->
            for (i in 0 until container.childCount) {
                (container.getChildAt(i) as? CheckBox)?.isChecked = false
            }
        }
        binding.fuelTypeContainer.let { container ->
            for (i in 0 until container.childCount) {
                (container.getChildAt(i) as? CheckBox)?.isChecked = false
            }
        }
    }

    private fun applyFilters() {
        val filter = CarFilter(
            carTypes = selectedCarTypes.toList(),
            priceRange = Pair(minPrice, maxPrice),
            rentalTime = selectedRentalTime,
            pickupDate = pickupDate,
            dropoffDate = dropoffDate,
            location = binding.locationEditText.text.toString(),
            colors = selectedColors.toList(),
            seatingCapacity = selectedSeatingCapacity.toList(),
            fuelTypes = selectedFuelTypes.toList()
        )
        onFilterApplied(filter)
        dismiss()
    }
}



