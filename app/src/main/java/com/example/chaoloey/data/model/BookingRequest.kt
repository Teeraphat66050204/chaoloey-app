package com.example.chaoloey.data.model

data class BookingRequest(
    val carId: Int,
    val startDate: String,
    val endDate: String
)

data class BookingResponse(
    val success: Boolean,
    val data: BookingData?
)

data class BookingData(
    val id: Int
)

data class BookingListResponse(
    val success: Boolean,
    val data: List<BookingItem>
)

data class SingleBookingResponse(
    val success: Boolean,
    val data: BookingItem?
)

data class BookingItem(
    val id: Int,
    val carId: Int,
    val startDate: String,
    val endDate: String,
    val totalPrice: Double,
    val status: String,
    val car: BookingCar
)

data class BookingCar(
    val id: Int,
    val name: String,
    val imageUrl: String
)
