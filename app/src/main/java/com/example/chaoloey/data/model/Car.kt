package com.example.chaoloey.data.model

data class Car(
    val id: Int,
    val name: String,
    val brand: String,
    val model: String,
    val year: Int,
    val seats: Int,
    val transmission: String,
    val fuelType: String,
    val category: String,
    val pricePerDay: Double,
    val imageUrl: String,
    val description: String?,
    val status: String,
    val createdAt: String,
    val updatedAt: String
)

data class CarListResponse(
    val success: Boolean,
    val data: List<Car>
)

data class CarDetailResponse(
    val success: Boolean,
    val data: Car
)
