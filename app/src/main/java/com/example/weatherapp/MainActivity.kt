package com.example.weatherapp

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import android.widget.SearchView 
import androidx.lifecycle.lifecycleScope
import com.example.weatherapp.databinding.ActivityMainBinding
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.text.SimpleDateFormat
import java.util.*




class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val apiKey = "58666d9bed6d4ee28a0b816a9f3f04b5"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setCurrentDate()
        setupSearch()
        loadWeather("Multan")
    }

    private fun setupSearch() {
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (!query.isNullOrEmpty()) {
                    loadWeather(query)
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return false
            }
        })
    }

    private fun loadWeather(city: String) {
        lifecycleScope.launch {
            fetchWeatherData(city)
        }
    }

    private suspend fun fetchWeatherData(city: String) {

        val api = Retrofit.Builder()
            .baseUrl("https://api.openweathermap.org/data/2.5/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiInterface::class.java)

        try {
            val response = api.getWeatherData(city, apiKey, "metric")

            binding.location.text = response.name
            binding.temp.text = response.main.temp.toString()
            binding.humiditynumber.text = response.main.humidity.toString()
            binding.windSpeed.text = response.wind.speed.toString()
            binding.sunrise.text = formatTime(response.sys.sunrise)
            binding.sunset.text = formatTime(response.sys.sunset)
            binding.sea.text = response.main.sea_level.toString()
            binding.maxTemp.text = "Max: ${response.main.temp_max}"
            binding.minTemp.text = "Min: ${response.main.temp_min}"
            binding.rain.text = response.weather[0].description
            val condition = response.weather[0].main
            binding.weatherCondition.text = condition
            updateUIByWeather(condition)


        } catch (e: Exception) {
            binding.temp.text = "Error"
            binding.location.text = "City not found"
        }
    }

    private fun updateUIByWeather(condition: String) {
        when (condition.lowercase(Locale.getDefault())) {
            "Sunny", "Clear" -> {
                binding.lottieAnimationView.setAnimation(R.raw.sun)
                binding.main.setBackgroundResource(R.drawable.sunny_background)
            }
            "clouds", "Mist", "Haze" -> {
                binding.lottieAnimationView.setAnimation(R.raw.cloud)
                binding.main.setBackgroundResource(R.drawable.colud_background)
            }
            "Rain", "Drizzle", "Thunderstorm" -> {
                binding.lottieAnimationView.setAnimation(R.raw.rain)
                binding.main.setBackgroundResource(R.drawable.rain_background)
            }
            "Snow" -> {
                binding.lottieAnimationView.setAnimation(R.raw.snow)
                binding.main.setBackgroundResource(R.drawable.snow_background)
            }
            else -> {
                binding.lottieAnimationView.setAnimation(R.raw.sun)
                binding.main.setBackgroundResource(R.drawable.sunny_background)


                   }





        }
    }

    private fun setCurrentDate() {
        val calendar = Calendar.getInstance()

        val dayFormat = SimpleDateFormat("EEEE", Locale.getDefault())
        val dateFormat = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())

        binding.day.text = dayFormat.format(calendar.time)
        binding.date.text = dateFormat.format(calendar.time)
    }

    private fun formatTime(timestamp: Long): String {
        val sdf = SimpleDateFormat("hh:mm a", Locale.getDefault())
        return sdf.format(Date(timestamp * 1000))
    }
}