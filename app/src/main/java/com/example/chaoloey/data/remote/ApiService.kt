package com.example.chaoloey.data.remote

import com.example.chaoloey.data.model.CarDetailResponse
import com.example.chaoloey.data.model.CarListResponse
import com.example.chaoloey.data.model.BookingListResponse
import com.example.chaoloey.data.model.BookingRequest
import com.example.chaoloey.data.model.BookingResponse
import com.example.chaoloey.data.model.LoginRequest
import com.example.chaoloey.data.model.LoginResponse
import com.example.chaoloey.data.model.RegisterRequest
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path

interface ApiService {

    @POST("auth/login")
    fun login(@Body request: LoginRequest): Call<LoginResponse>

    @POST("auth/register")
    fun register(@Body request: RegisterRequest): Call<LoginResponse>

    @GET("cars")
    fun getCars(): Call<CarListResponse>

    @GET("cars/{id}")
    fun getCarById(@Path("id") carId: Int): Call<CarDetailResponse>

    @POST("bookings")
    fun createBooking(
        @Header("Authorization") authorization: String,
        @Body request: BookingRequest
    ): Call<BookingResponse>

    @GET("bookings")
    fun getMyBookings(
        @Header("Authorization") authorization: String
    ): Call<BookingListResponse>
}
