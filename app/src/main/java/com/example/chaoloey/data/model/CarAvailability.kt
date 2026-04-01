package com.example.chaoloey.data.model

data class CarAvailabilityResponse(
    val success: Boolean,
    val data: CarAvailabilityData?
)

data class CarAvailabilityData(
    val carId: Int,
    val from: String,
    val to: String,
    val blockedDates: List<String>   // format: "yyyy-MM-dd"
)

