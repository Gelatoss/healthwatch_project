package com.healthwatch.healthapp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.healthwatch.healthapp.model.ApiClient
import com.healthwatch.healthapp.model.DataAccessResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomeViewModel : ViewModel() {
    private val _logoutResult = MutableLiveData<Boolean>()
    val logoutResult: LiveData<Boolean> = _logoutResult

    fun logoutUser(token: String?) {
        val authToken = "Bearer $token"
        ApiClient.apiService.logout(authToken)
            .enqueue(object : Callback<Void> {
                override fun onResponse(call: Call<Void>, response: Response<Void>) {
                    if (response.isSuccessful) {
                        _logoutResult.postValue(true)
                    } else {
                        _logoutResult.postValue(false)
                    }
                }

                override fun onFailure(call: Call<Void>, t: Throwable) {
                    _logoutResult.postValue(false)
                }
            })
    }
}