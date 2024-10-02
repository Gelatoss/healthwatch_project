package com.healthwatch.healthapp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.healthwatch.healthapp.model.ApiClient
import com.healthwatch.healthapp.model.AuthenticationRequest
import com.healthwatch.healthapp.model.AuthenticationResponse
import com.healthwatch.healthapp.model.SharedPreferencesManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginViewModel : ViewModel() {
    private val _loginResult = MutableLiveData<String>()
    val loginResult: LiveData<String>
        get() = _loginResult

    fun login(email: String, password: String, sharedPreferencesManager: SharedPreferencesManager) {
        val request = AuthenticationRequest(email, password)
        ApiClient.apiService.loginUser(request)
            .enqueue(object : Callback<AuthenticationResponse> {
                override fun onResponse(call: Call<AuthenticationResponse>, response: Response<AuthenticationResponse>) {
                    if (response.isSuccessful) {
                        val token = response.body()?.token
                        if (!token.isNullOrEmpty()) {
                            sharedPreferencesManager.saveAuthToken(token)
                            _loginResult.postValue("Login successful!")
                        } else {
                            _loginResult.postValue("Login failed: No token received")
                        }
                    } else {
                        _loginResult.postValue("Login failed!")
                    }
                }

                override fun onFailure(call: Call<AuthenticationResponse>, t: Throwable) {
                    _loginResult.postValue("Error: ${t.message}")
                }
            })
    }
}
