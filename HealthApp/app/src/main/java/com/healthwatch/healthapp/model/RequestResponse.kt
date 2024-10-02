package com.healthwatch.healthapp.model

data class AuthenticationRequest(
    val email: String,
    val password: String
)

data class RegisterRequest(
    val firstname: String,
    val lastname: String,
    val email: String,
    val password: String
)

data class AuthenticationResponse(
    val token: String
)

data class UserInfoResponse(
    val firstname: String,
    val lastname: String
)

data class UserExtra(
    val age: Int?,
    val height: Int?,
    val weight: Float?,
    val country: String?,
    val city: String?,
    val street: String?,
    val phone: String?
)


data class UserExtraCodeResponse(
    val errorCode: Int
)

data class resetRequest(
    val newPassword: String
)

data class resetResponse(
    val message: String
)

data class Data(
    val averageHeartRate: Double,
    val totalSteps: Long,
    val timeStamp: String
)

data class DataAccessResponse(
    val data: List<Data>
)


