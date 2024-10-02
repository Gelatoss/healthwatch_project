package com.healthwatch.healthapp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.healthwatch.healthapp.model.AuthenticationResponse
import com.healthwatch.healthapp.model.ApiService
import com.healthwatch.healthapp.model.RegisterRequest
import com.healthwatch.healthapp.model.RetrofitClient
import com.healthwatch.healthapp.model.SharedPreferencesManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegisterViewModel : ViewModel() {
    private val _registrationResult = MutableLiveData<String>()
    val registrationResult: LiveData<String>
        get() = _registrationResult

    fun register(firstName: String, lastName: String, email: String, password: String, sharedPreferencesManager: SharedPreferencesManager) {
        val request = RegisterRequest(firstName, lastName, email, password)
        RetrofitClient.retrofit.create(ApiService::class.java).registerUser(request)
            .enqueue(object : Callback<AuthenticationResponse> {
                override fun onResponse(call: Call<AuthenticationResponse>, response: Response<AuthenticationResponse>) {
                    if (response.isSuccessful) {
                        val token = response.body()?.token
                        if(!token.isNullOrEmpty()) {
                            sharedPreferencesManager.saveAuthToken(token)
                            _registrationResult.postValue("Registration successful!")
                        } else {
                            _registrationResult.postValue("Registration failed: No token received")
                        }
                    } else {
                        val errorMessage = response.errorBody()?.string() ?: "Unknown error"
                        _registrationResult.postValue("Registration failed: $errorMessage")
                    }
                }

                override fun onFailure(call: Call<AuthenticationResponse>, t: Throwable) {
                    _registrationResult.postValue("Network error: ${t.message}")
                }
            })
    }
}
