package com.healthwatch.healthapp.view

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.healthwatch.healthapp.MainActivity
import com.healthwatch.healthapp.R
import com.healthwatch.healthapp.model.SharedPreferencesManager
import com.healthwatch.healthapp.viewmodel.HomeViewModel

class HomeActivity : AppCompatActivity() {
    private lateinit var viewModel: HomeViewModel
    private lateinit var sharedPreferencesManager: SharedPreferencesManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_home)

        sharedPreferencesManager = SharedPreferencesManager(this)
        viewModel = ViewModelProvider(this)[HomeViewModel::class.java]

        val accountButton: Button = findViewById(R.id.accountButton)
        val dataButton: Button = findViewById(R.id.dataButton)
        val btButton: Button = findViewById(R.id.btButton)
        val logoutButton: Button = findViewById(R.id.logoutButton)

        viewModel.logoutResult.observe(this) { success ->
            if (success) {
                Toast.makeText(this, "Logout successful", Toast.LENGTH_SHORT).show()
                navigateToLogin()
            } else {
                Toast.makeText(this, "Failed to logout", Toast.LENGTH_SHORT).show()
            }
        }

        val token = sharedPreferencesManager.getAuthToken()

        logoutButton.setOnClickListener {
            viewModel.logoutUser(token)
        }

        dataButton.setOnClickListener {
            navigateToData()
        }

        btButton.setOnClickListener {
            navigateToBt()
        }

        accountButton.setOnClickListener {
            navigateToAccount()
        }
    }

    private fun navigateToAccount() {
        val accountIntent = Intent(this, AccountActivity::class.java)
        accountIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(accountIntent)
        finish()
    }

    private fun navigateToLogin() {
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    private fun navigateToBt() {
        val btIntent = Intent(this, ComposeActivity::class.java)
        btIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(btIntent)
        finish()
    }


    private fun navigateToData(){
        val intent = Intent(this, ChartActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}
