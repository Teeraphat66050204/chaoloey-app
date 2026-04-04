package com.example.chaoloey.data.model

data class CarFilter(
    val carTypes: List<String> = emptyList(),
    val priceRange: Pair<Int, Int>? = null,
    val rentalTime: String? = null,
    val pickupDate: String? = null,
    val dropoffDate: String? = null,
    val location: String? = null,
    val colors: List<String> = emptyList(),
    val seatingCapacity: List<Int> = emptyList(),
    val fuelTypes: List<String> = emptyList()
)


