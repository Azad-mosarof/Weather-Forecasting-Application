package com.example.weatherapplication

import android.Manifest
import android.content.pm.PackageManager
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.core.app.ActivityCompat
import com.example.weatherapplication.databinding.ActivityMainBinding
import org.json.JSONObject
import java.net.URL
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    val API: String = "ec36446c9e0b45583618f97dea0d9761"
    private var cityName: String = "Durgapur"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        if(ActivityCompat.checkSelfPermission(this,Manifest.permission.INTERNET)
            != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.INTERNET),101)
        }
        else{
            weatherTask().execute()
            binding.searchBtn.setOnClickListener{
                cityName = binding.cityName.text.toString()
                weatherTask().execute()
            }
        }

    }

    inner class weatherTask() : AsyncTask<String, Void, String>()
    {
        override fun onPreExecute() {
            super.onPreExecute()
            binding.loader.visibility = View.VISIBLE
            binding.mainContainer.visibility = View.GONE
            binding.errorText.visibility = View.GONE
        }

        override fun doInBackground(vararg params: String?): String? {
            var response: String?
            try{
                response = URL("https://api.openweathermap.org/data/2.5/weather?q=$cityName&units=metric&appid=$API")
                    .readText(Charsets.UTF_8)
            }
            catch (e:Exception){
                response = null
            }
            return response
        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)
            try{
                val jsonObj = JSONObject(result)
                val main = jsonObj.getJSONObject("main")
                val sys = jsonObj.getJSONObject("sys")
                val wind = jsonObj.getJSONObject("wind")
                val weather = jsonObj.getJSONArray("weather").getJSONObject(0)
                val updateAt = jsonObj.getLong("dt")
                val updateAtText = "Updated at:" + SimpleDateFormat("dd/MM/yyyy hh:mm a", Locale.ENGLISH).format(Date(updateAt*100))
                val temp = main.getString("temp")+"°C"
                val temMin = "Min Temp: "+main.getString("temp_min")+"°C"
                val maxTemp = "Max Temp: "+main.getString("temp_max")+"°C"
                val pressure = main.getString("pressure")
                val humidity = main.getString("humidity")
                val sunRise = sys.getLong("sunrise")
                val sunSet = sys.getLong("sunset")
                val windSpeed = wind.getString("speed")
                val weatherDescription = weather.getString("description")
                val address = jsonObj.getString("name")+", "+sys.getString("country")

                binding.location.text = address
                binding.updateAt.text = updateAtText
                binding.status.text = weatherDescription.capitalize()
                binding.temp.text = temp
                binding.minTemp.text = temMin
                binding.maxTemp.text = maxTemp
                binding.sunRise.text = SimpleDateFormat("hh:mm a", Locale.ENGLISH).format(Date(sunRise*1000))
                binding.sunSet.text = SimpleDateFormat("hh:mm a", Locale.ENGLISH).format(Date(sunSet*1000))
                binding.wind.text = windSpeed
                binding.humidity.text = humidity
                binding.pressure.text = pressure
                binding.info.text = weatherDescription.capitalize()

                binding.loader.visibility = View.GONE
                binding.mainContainer.visibility = View.VISIBLE
                binding.errorText.visibility = View.GONE
            }
            catch (e: Exception){
                binding.loader.visibility = View.GONE
                binding.errorText.visibility = View.VISIBLE
            }
        }
    }
}