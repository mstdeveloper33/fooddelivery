package com.mst.fooddelivery.data

import com.mst.fooddelivery.data.models.AuthResponse
import com.mst.fooddelivery.data.models.SignUpRequest
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface FoodApi {

    @GET("/food")
    suspend fun getFood(): List<String>

    @POST("/auth/signup")
    suspend fun signUp(@Body request: SignUpRequest): AuthResponse

}