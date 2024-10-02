package com.healthwatch.healthapp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.healthwatch.healthapp.model.ApiClient
import com.healthwatch.healthapp.model.UserExtra
import com.healthwatch.healthapp.model.UserExtraCodeResponse
import com.healthwatch.healthapp.model.UserInfoResponse
import com.healthwatch.healthapp.model.resetRequest
import com.healthwatch.healthapp.model.resetResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AccountViewModel : ViewModel() {
    private val _userInfo = MutableLiveData<UserExtra>()
    val userInfo: LiveData<UserExtra> get() = _userInfo

    private val _updateResult = MutableLiveData<String>()
    val updateResult: LiveData<String> get() = _updateResult

    private val _resetResult = MutableLiveData<String>()
    val resetResult: LiveData<String> get() = _resetResult

    private val _userNameInfo = MutableLiveData<UserInfoResponse>()
    val userNameInfo: LiveData<UserInfoResponse> get() = _userNameInfo

    fun getInfoUser(token: String?) {
        val authToken = "Bearer $token"
        ApiClient.apiService.getInfo(authToken)
            .enqueue(object : Callback<UserExtra> {
            override fun onResponse(call: Call<UserExtra>, response: Response<UserExtra>) {
                if (response.isSuccessful) {
                    _userInfo.postValue(response.body())
                } else {
                    _updateResult.postValue("Failed to fetch user info")
                }
            }

            override fun onFailure(call: Call<UserExtra>, t: Throwable) {
                _updateResult.postValue("Error: ${t.message}")
            }
        })
    }

    fun updateUserExtra(token: String?, userExtra: UserExtra) {
        val authToken = "Bearer $token"
        ApiClient.apiService.updateInfo(authToken, userExtra).enqueue(object : Callback<UserExtraCodeResponse> {
            override fun onResponse(call: Call<UserExtraCodeResponse>, response: Response<UserExtraCodeResponse>) {
                if (response.isSuccessful) {
                    _updateResult.postValue("Update successful")
                } else {
                    _updateResult.postValue("Update failed")
                }
            }

            override fun onFailure(call: Call<UserExtraCodeResponse>, t: Throwable) {
                _updateResult.postValue("Error: ${t.message}")
            }
        })
    }

    fun resetPassword(token: String?, password: String) {
        val authToken = "Bearer $token"
        val request = resetRequest(password)
        ApiClient.apiService.resetPassword(authToken, request).enqueue(object : Callback<resetResponse> {
            override fun onResponse(call: Call<resetResponse>, response: Response<resetResponse>) {
                if (response.isSuccessful) {
                    _resetResult.postValue("Password reset successful")
                } else {
                    _resetResult.postValue("Password reset failed")
                }
            }

            override fun onFailure(call: Call<resetResponse>, t: Throwable) {
                _resetResult.postValue("Error: ${t.message}")
            }
        })
    }

    fun userInfo(token: String?) {
        val authToken = "Bearer $token"
        ApiClient.apiService.userInfo(authToken)
            .enqueue(object : Callback<UserInfoResponse> {
                override fun onResponse(
                    call: Call<UserInfoResponse>,
                    response: Response<UserInfoResponse>
                ) {
                    if (response.isSuccessful) {
                        _userNameInfo.postValue(response.body())
                    } else {
                        _updateResult.postValue("Failed to fetch username info")
                    }
                }

                override fun onFailure(call: Call<UserInfoResponse>, t: Throwable) {
                    _updateResult.postValue("Error: ${t.message}")
                }
            })
    }
}
