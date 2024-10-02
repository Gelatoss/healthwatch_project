package com.healthwatch.healthapp.view

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.ViewModelProvider
import com.healthwatch.healthapp.R
import com.healthwatch.healthapp.model.SharedPreferencesManager
import com.healthwatch.healthapp.model.UserExtra
import com.healthwatch.healthapp.model.UserInfoResponse
import com.healthwatch.healthapp.viewmodel.AccountViewModel

class AccountActivity : AppCompatActivity() {

    private lateinit var firstName: TextView
    private lateinit var lastName: TextView
    private lateinit var infoContainer: LinearLayout
    private lateinit var btnChange: Button
    private lateinit var btnSave: Button
    private lateinit var btnCancel: Button
    private lateinit var actionButtonsContainer: LinearLayout
    private lateinit var age: EditText
    private lateinit var height: EditText
    private lateinit var weight: EditText
    private lateinit var country: EditText
    private lateinit var city: EditText
    private lateinit var street: EditText
    private lateinit var phone: EditText
    private lateinit var accountViewModel: AccountViewModel
    private lateinit var sharedPreferencesManager: SharedPreferencesManager

    private lateinit var btnResetPassword: Button
    private lateinit var btnConfirmReset: Button
    private lateinit var btnCancelReset: Button
    private lateinit var actionResetContainer: LinearLayout
    private lateinit var passwordResetContainer: LinearLayout
    private lateinit var newPassword: EditText
    private lateinit var confirmPassword: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_account)

        initViews()
        setupToolbar()
        setupViewModel()

        btnChange.setOnClickListener { toggleEditMode(true) }
        btnSave.setOnClickListener { saveUserExtra() }
        btnCancel.setOnClickListener { toggleEditMode(false) }
        btnResetPassword.setOnClickListener { toggleResetMode(true) }
        btnConfirmReset.setOnClickListener { confirmResetPassword() }
        btnCancelReset.setOnClickListener { cancelResetPassword() }

        fetchUserInfo()
    }

    private fun initViews() {
        firstName = findViewById(R.id.firstName)
        lastName = findViewById(R.id.secondName)
        infoContainer = findViewById(R.id.infoContainer)
        btnChange = findViewById(R.id.btnChange)
        btnSave = findViewById(R.id.btnSave)
        btnCancel = findViewById(R.id.btnCancel)
        actionButtonsContainer = findViewById(R.id.actionButtonsContainer)
        age = findViewById(R.id.age)
        height = findViewById(R.id.height)
        weight = findViewById(R.id.weight)
        country = findViewById(R.id.country)
        city = findViewById(R.id.city)
        street = findViewById(R.id.street)
        phone = findViewById(R.id.phone)

        btnResetPassword = findViewById(R.id.btnResetPassword)
        btnConfirmReset = findViewById(R.id.btnConfirmReset)
        btnCancelReset = findViewById(R.id.btnCancelReset)
        actionResetContainer = findViewById(R.id.actionResetContainer)
        passwordResetContainer = findViewById(R.id.passwordResetContainer)
        newPassword = findViewById(R.id.newPassword)
        confirmPassword = findViewById(R.id.confirmPassword)
    }

    private fun setupToolbar() {
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.title = "Account"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        toolbar.setNavigationOnClickListener { navigateToHome() }
    }

    private fun setupViewModel() {
        sharedPreferencesManager = SharedPreferencesManager(this)
        accountViewModel = ViewModelProvider(this)[AccountViewModel::class.java]

        accountViewModel.userNameInfo.observe(this) { userinfo ->
            userinfo?.let { populateUserName(it) }
        }

        accountViewModel.userInfo.observe(this) { userExtra ->
            userExtra?.let { populateUserInfo(it) }
        }

        accountViewModel.updateResult.observe(this) { result ->
            handleUpdateResult(result)
        }

        accountViewModel.resetResult.observe(this) { result ->
            handleResetResult(result)
        }
    }

    private fun fetchUserInfo() {
        val token = sharedPreferencesManager.getAuthToken()
        accountViewModel.userInfo(token)
        accountViewModel.getInfoUser(token)
    }

    private fun saveUserExtra() {
        val userExtra = UserExtra(
            age = age.text.toString().toIntOrNull(),
            height = height.text.toString().toIntOrNull(),
            weight = weight.text.toString().toFloatOrNull(),
            country = country.text.toString().takeIf { it.isNotBlank() },
            city = city.text.toString().takeIf { it.isNotBlank() },
            street = street.text.toString().takeIf { it.isNotBlank() },
            phone = phone.text.toString().takeIf { it.isNotBlank() }
        )
        val token = sharedPreferencesManager.getAuthToken()
        accountViewModel.updateUserExtra(token, userExtra)
    }

    private fun confirmResetPassword() {
        val password = newPassword.text.toString()
        val confirmPasswordText = confirmPassword.text.toString()
        if (password == confirmPasswordText) {
            val token = sharedPreferencesManager.getAuthToken()
            accountViewModel.resetPassword(token, password)
            clearResetFields()
        } else {
            Toast.makeText(this, "Passwords do not match", Toast.LENGTH_LONG).show()
        }
    }

    private fun cancelResetPassword() {
        toggleResetMode(false)
        clearResetFields()
    }

    private fun navigateToHome() {
        val homeIntent = Intent(this, HomeActivity::class.java)
        homeIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(homeIntent)
        finish()
    }

    private fun toggleEditMode(enabled: Boolean) {
        btnChange.visibility = if (enabled) View.GONE else View.VISIBLE
        actionButtonsContainer.visibility = if (enabled) View.VISIBLE else View.GONE

        setEditableFieldsEnabled(enabled)
        infoContainer.alpha = if (enabled) 1.0f else 0.8f
    }

    private fun setEditableFieldsEnabled(enabled: Boolean) {
        val fields = listOf(age, height, weight, country, city, street, phone)
        fields.forEach { field ->
            field.isEnabled = enabled
            field.isFocusable = enabled
            field.isFocusableInTouchMode = enabled
        }
    }

    private fun populateUserName(userInfo: UserInfoResponse) {
        firstName.text = userInfo.firstname
        lastName.text = userInfo.lastname
    }

    private fun populateUserInfo(userExtra: UserExtra) {
        age.setText(userExtra.age?.toString() ?: "")
        height.setText(userExtra.height?.toString() ?: "")
        weight.setText(userExtra.weight?.toString() ?: "")
        country.setText(userExtra.country ?: "")
        city.setText(userExtra.city ?: "")
        street.setText(userExtra.street ?: "")
        phone.setText(userExtra.phone ?: "")
    }

    private fun toggleResetMode(enabled: Boolean) {
        btnResetPassword.visibility = if (enabled) View.GONE else View.VISIBLE
        actionResetContainer.visibility = if (enabled) View.VISIBLE else View.GONE

        newPassword.isEnabled = enabled
        newPassword.isFocusable = enabled
        newPassword.isFocusableInTouchMode = enabled

        confirmPassword.isEnabled = enabled
        confirmPassword.isFocusable = enabled
        confirmPassword.isFocusableInTouchMode = enabled

        passwordResetContainer.alpha = if (enabled) 1.0f else 0.8f
    }

    private fun clearResetFields() {
        newPassword.setText("")
        confirmPassword.setText("")
    }

    private fun handleUpdateResult(result: String) {
        if (result == "Update successful") {
            toggleEditMode(false)
            accountViewModel.getInfoUser(sharedPreferencesManager.getAuthToken())
        } else {
            Toast.makeText(this, result, Toast.LENGTH_LONG).show()
        }
    }

    private fun handleResetResult(result: String) {
        if (result == "Password reset successful") {
            toggleResetMode(false)
            Toast.makeText(this, "Password reset successful", Toast.LENGTH_LONG).show()
        } else {
            Toast.makeText(this, result, Toast.LENGTH_LONG).show()
        }
    }
}
