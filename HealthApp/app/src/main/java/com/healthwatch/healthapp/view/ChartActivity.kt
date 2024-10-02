package com.healthwatch.healthapp.view

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter
import com.google.gson.Gson
import com.healthwatch.healthapp.R
import com.healthwatch.healthapp.model.ApiClient
import com.healthwatch.healthapp.model.Data
import com.healthwatch.healthapp.model.DataAccessResponse
import com.healthwatch.healthapp.model.SharedPreferencesManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class DateValueFormatter : ValueFormatter() {
    private val dateFormat = SimpleDateFormat("HH:mm", Locale.getDefault())

    init {
        dateFormat.timeZone = TimeZone.getTimeZone("UTC")
    }

    override fun getFormattedValue(value: Float): String {
        return dateFormat.format(Date(value.toLong()))
    }
}

class DateValueFormatter2 : ValueFormatter() {
    private val dateFormat = SimpleDateFormat("MM/dd", Locale.getDefault())

    override fun getFormattedValue(value: Float): String {
        val date = Date(value.toLong())
        dateFormat.timeZone = TimeZone.getDefault() // Use local time zone for display
        return dateFormat.format(date)
    }
}

class ChartActivity : AppCompatActivity() {
    private lateinit var chart: LineChart
    private lateinit var timeframeGroup: RadioGroup
    private lateinit var dataTypeGroup: RadioGroup
    private lateinit var submitButton: Button
    private lateinit var clearButton: Button
    private lateinit var sharedPreferencesManager: SharedPreferencesManager
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chart)

        sharedPreferencesManager = SharedPreferencesManager(this) // Initialize SharedPreferencesManager

        initializeUI()
        setupListeners()
        setupToolbar()
    }

    private fun setupToolbar() {
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.title = "Data"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        toolbar.setNavigationOnClickListener { navigateToHome() }
    }

    private fun navigateToHome() {
        val homeIntent = Intent(this, HomeActivity::class.java)
        homeIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(homeIntent)
        finish()
    }

    private fun initializeUI() {
        chart = findViewById(R.id.line_chart)
        timeframeGroup = findViewById(R.id.timeframeGroup)
        dataTypeGroup = findViewById(R.id.dataTypeGroup)
        submitButton = findViewById(R.id.submitButton)
        clearButton = findViewById(R.id.clearButton)
    }

    private fun setupListeners() {
        submitButton.setOnClickListener {
            val selectedTimeframeId = timeframeGroup.checkedRadioButtonId
            val selectedDataTypeId = dataTypeGroup.checkedRadioButtonId
            if (selectedTimeframeId != -1 && selectedDataTypeId != -1) {
                val selectedTimeframe =
                    findViewById<RadioButton>(selectedTimeframeId).text.toString()
                val selectedDataType = findViewById<RadioButton>(selectedDataTypeId).text.toString()

                chart.clear()
                chart.xAxis.resetAxisMinimum()
                chart.xAxis.resetAxisMaximum()
                chart.invalidate()

                updateChart(selectedTimeframe, selectedDataType)
            } else {
                Toast.makeText(
                    this,
                    "Please select both a timeframe and a data type.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        clearButton.setOnClickListener {
            chart.clear()
        }
    }

    private fun updateChart(timeframe: String, dataType: String) {
        val token = sharedPreferencesManager.getAuthToken()
        when (timeframe.lowercase(Locale.ROOT)) {
            "daily" -> fetchDailyData(token, dataType, timeframe)
            "weekly" -> fetchWeeklyData(token, dataType, timeframe)
            "monthly" -> fetchMonthlyData(token, dataType, timeframe)
            else -> Toast.makeText(this, "Please select a valid timeframe", Toast.LENGTH_LONG).show()
        }
    }


    private fun fetchDailyData(token: String?, dataType: String, timeframe: String) {
        val authToken = "Bearer $token"
        ApiClient.apiService.fetchDailyData(authToken)
            .enqueue(object : Callback<DataAccessResponse> {
                override fun onResponse(
                    call: Call<DataAccessResponse>,
                    response: Response<DataAccessResponse>
                ) {
                    if (response.isSuccessful) {
                        Log.d("ChartActivity", "Data fetched successfully")
                        response.body()?.let {
                            val gson = Gson()
                            val jsonResponse = gson.toJson(it)
                            Log.d("ChartActivity", "JSON Response: $jsonResponse")
                            updateChartWithData(it.data, dataType, timeframe)
                        }
                    } else {
                        Log.e(
                            "ChartActivity",
                            "Failed to fetch data: ${response.code()} - ${response.message()}"
                        )
                        Toast.makeText(
                            this@ChartActivity,
                            "Failed to fetch data",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onFailure(call: Call<DataAccessResponse>, t: Throwable) {
                    Log.e("ChartActivity", "Error: ${t.message}", t)
                    Toast.makeText(this@ChartActivity, "Error: ${t.message}", Toast.LENGTH_SHORT)
                        .show()
                }
            })
    }

    private fun fetchWeeklyData(token: String?, dataType: String, timeframe: String) {
        val authToken = "Bearer $token"
        ApiClient.apiService.fetchWeeklyData(authToken)
            .enqueue(object : Callback<DataAccessResponse> {
                override fun onResponse(
                    call: Call<DataAccessResponse>,
                    response: Response<DataAccessResponse>
                ) {
                    if (response.isSuccessful) {
                        Log.d("ChartActivity", "Weekly data fetched successfully")
                        response.body()?.let {
                            val gson = Gson()
                            val jsonResponse = gson.toJson(it)
                            Log.d("ChartActivity", "JSON Response: $jsonResponse")
                            updateChartWithData(it.data, dataType, timeframe)
                        }
                    } else {
                        Log.e("ChartActivity", "Failed to fetch weekly data: ${response.code()} - ${response.message()}")
                        Toast.makeText(this@ChartActivity, "Failed to fetch weekly data", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<DataAccessResponse>, t: Throwable) {
                    Log.e("ChartActivity", "Error: ${t.message}", t)
                    Toast.makeText(this@ChartActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
    }

    private fun fetchMonthlyData(token: String?, dataType: String, timeframe: String) {
        val authToken = "Bearer $token"
        ApiClient.apiService.fetchMonthlyData(authToken)
            .enqueue(object : Callback<DataAccessResponse> {
                override fun onResponse(
                    call: Call<DataAccessResponse>,
                    response: Response<DataAccessResponse>
                ) {
                    if (response.isSuccessful) {
                        Log.d("ChartActivity", "Monthly data fetched successfully")
                        response.body()?.let {
                            val gson = Gson()
                            val jsonResponse = gson.toJson(it)
                            Log.d("ChartActivity", "JSON Response: $jsonResponse")
                            updateChartWithData(it.data, dataType, timeframe)
                        }
                    } else {
                        Log.e("ChartActivity", "Failed to fetch monthly data: ${response.code()} - ${response.message()}")
                        Toast.makeText(this@ChartActivity, "Failed to fetch monthly data", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<DataAccessResponse>, t: Throwable) {
                    Log.e("ChartActivity", "Error: ${t.message}", t)
                    Toast.makeText(this@ChartActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
    }

    private fun updateChartWithData(data: List<Data>, dataType: String, timeframe: String) {
        val entries = ArrayList<Entry>()
        val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
        dateFormat.timeZone = TimeZone.getTimeZone("UTC")

        data.forEach { dataPoint ->
            Log.d("ChartActivity", "Data point: ${dataPoint.timeStamp}, Heartbeat: ${dataPoint.averageHeartRate}, Steps: ${dataPoint.totalSteps}")
            try {
                val date = dateFormat.parse(dataPoint.timeStamp)
                date?.let { parsedDate ->
                    val value = when (dataType.lowercase(Locale.ROOT)) {
                        "heartbeat" -> dataPoint.averageHeartRate
                        "steps" -> dataPoint.totalSteps
                        else -> {
                            Log.e("ChartActivity", "Invalid data type: $dataType")
                            return@forEach
                        }
                    }
                    value.let { num ->
                        entries.add(Entry(parsedDate.time.toFloat(), num.toFloat()))
                    }
                }
            } catch (e: Exception) {
                Log.e("ChartActivity", "Error parsing date: ${dataPoint.timeStamp}", e)
            }
        }

        if (entries.isEmpty()) {
            Log.e("ChartActivity", "No data entries to display")
            return
        }

        val dataSet = LineDataSet(entries, dataType)
        dataSet.lineWidth = 2.5f
        dataSet.color = getColor(R.color.white)
        dataSet.valueTextColor = getColor(R.color.white)
        dataSet.valueTextSize = 11f

        chart.data = LineData(dataSet)
        setupXAxis(entries, timeframe)
        chart.invalidate()
        chart.requestLayout()
    }

    private fun setupXAxis(entries: ArrayList<Entry>,timeframe: String) {
        val xAxis = chart.xAxis
        chart.xAxis.resetAxisMinimum()
        chart.xAxis.resetAxisMaximum()

//        if (entries.isNotEmpty()) {
//            val minX = entries.minByOrNull { it.x }?.x ?: 0f
//            val maxX = entries.maxByOrNull { it.x }?.x ?: 0f
//            xAxis.axisMinimum = minX
//            xAxis.axisMaximum = maxX
//        }
        when (timeframe.lowercase(Locale.ROOT)) {
            "daily" -> {
                xAxis.valueFormatter = DateValueFormatter()
                xAxis.granularity = 3600000f
                xAxis.labelCount = 24
            }
            "weekly" -> {
                xAxis.valueFormatter = DateValueFormatter2()
                xAxis.granularity = 86400000f
            }
            "monthly" -> {
                xAxis.valueFormatter = DateValueFormatter2()
                xAxis.granularity = 86400000f
            }
            else -> Toast.makeText(this, "Please select a valid timeframe", Toast.LENGTH_LONG).show()
        }

        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.setLabelCount(entries.size, true)
        xAxis.labelRotationAngle = -45f
        xAxis.textColor = getColor(R.color.white)
    }
}

