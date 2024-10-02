package com.healthwatch.healthapp.model

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.GET

interface ApiService {
    @POST("auth/authenticate")
    fun loginUser(@Body request: AuthenticationRequest): Call<AuthenticationResponse>

    @POST("auth/register")
    fun registerUser(@Body request: RegisterRequest): Call<AuthenticationResponse>

    @GET("auth/logout")
    fun logout(@Header("Authorization") token: String): Call<Void>

    @GET("extra/get")
    fun getInfo(@Header("Authorization") token: String): Call<UserExtra>

    @POST("extra/add")
    fun updateInfo(@Header("Authorization") token: String, @Body request: UserExtra): Call<UserExtraCodeResponse>

    @GET("extra/info")
    fun userInfo(@Header("Authorization") token: String): Call<UserInfoResponse>

    @POST("auth/reset")
    fun resetPassword(@Header("Authorization") token: String, @Body request: resetRequest): Call<resetResponse>

    @GET("data_access/daily")
    fun fetchDailyData(@Header("Authorization") token: String): Call<DataAccessResponse>

    @GET("data_access/weekly")
    fun fetchWeeklyData(@Header("Authorization") token: String): Call<DataAccessResponse>

    @GET("data_access/monthly")
    fun fetchMonthlyData(@Header("Authorization") token: String): Call<DataAccessResponse>

}
