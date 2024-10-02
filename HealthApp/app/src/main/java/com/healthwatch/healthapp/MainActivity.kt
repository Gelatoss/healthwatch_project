package com.healthwatch.healthapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.healthwatch.healthapp.model.SharedPreferencesManager
import com.healthwatch.healthapp.view.HomeActivity
import com.healthwatch.healthapp.view.LoginActivity
import com.healthwatch.healthapp.view.RegisterActivity

class MainActivity : AppCompatActivity() {
    private lateinit var sharedPreferencesManager: SharedPreferencesManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        sharedPreferencesManager = SharedPreferencesManager(this)

        if (sharedPreferencesManager.isUserLoggedIn(this, 24)) {
            val homeIntent = Intent(this, HomeActivity::class.java)
            startActivity(homeIntent)
            finish()
        } else {
            findViewById<Button>(R.id.loginButton).setOnClickListener {
                startActivity(Intent(this, LoginActivity::class.java))
                //startActivity(Intent(this, HomeActivity::class.java))
            }

            findViewById<Button>(R.id.registerButton).setOnClickListener {
                startActivity(Intent(this, RegisterActivity::class.java))
            }
        }
    }
}