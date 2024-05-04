package org.duckdns.fouquet.equiendurance

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat


class MainActivity : AppCompatActivity() {
    private var textViewSpeedAverage: TextView? = null
    private var textViewSpeedInst: TextView? = null
    private var textViewDistance: TextView? = null
    private var textViewTemps: TextView? = null
    private var locationManager: LocationManager? = null
    private var locationListener: LocationListener? = null
    private var locationList: MutableList<Location>? = null
    private var timeList: MutableList<Long>? = null
    private var speedList: MutableList<Float>? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        textViewSpeedAverage = findViewById(R.id.text_view_speed_average)
        textViewSpeedInst = findViewById(R.id.text_view_speed_inst)
        textViewDistance = findViewById(R.id.text_view_distance)
        textViewTemps = findViewById(R.id.text_view_time)
        locationManager = getSystemService(LOCATION_SERVICE) as LocationManager
        locationList = ArrayList()
        timeList = ArrayList()
        speedList = ArrayList()
        locationListener = object : LocationListener {
            override fun onLocationChanged(location: Location) {
                (locationList as ArrayList<Location>).add(location)
                (speedList as ArrayList<Float>).add(location.speed)
                (timeList as ArrayList<Long>).add(System.currentTimeMillis())
                val speedAverage = calculateAverageSpeed()
                val speedInst = calculateInstSpeed()
                val temps = calculateTime()
                val distance = calculateDistance()
                (textViewSpeedAverage as TextView).text =
                    "Vitesse moyenne : " + String.format("%.2f", speedAverage) + " km/h"
                (textViewSpeedInst as TextView).text =
                    "Vitesse Instantanée : " + String.format("%.2f", speedInst) + " km/h"
                (textViewDistance as TextView).text =
                    "Distance : " + String.format("%.2f", distance) + " km"
                (textViewTemps as TextView).text =
                    "Temps : " + String.format("%.2f", temps) + " h"
            }

            override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {}
            override fun onProviderEnabled(provider: String) {}
            override fun onProviderDisabled(provider: String) {}
        }

        findViewById<Button>(R.id.button_raz)
            .setOnClickListener {
                (locationList as ArrayList<Location>).clear()
                (timeList as ArrayList<Long>).clear()
                (speedList as ArrayList<Float>).clear()
            }

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                1
            )
        } else {
            locationManager!!.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                0,
                0f,
                locationListener as LocationListener
            )
        }
    }

    private fun calculateAverageSpeed(): Float {
        if (locationList!!.size < 2) {
            return 0f
        }
        var totalDistance = 0f
        var totalTime: Long = 0
        var speedTotal = 0f
        for (i in 0 until locationList!!.size - 1) {
            totalDistance += locationList!![i].distanceTo(locationList!![i + 1])
            totalTime += timeList!![i + 1] - timeList!![i]
            speedTotal += speedList!![i]
        }
        //return (totalDistance / (totalTime / 1000f))*(1000f/60f)
        return speedTotal/ locationList!!.size
    }
    private fun calculateTime(): Float {
        if (locationList!!.size < 2) {
            return 0f
        }
        var totalDistance = 0f
        var totalTime: Long = 0
        var speedTotal = 0f
        for (i in 0 until locationList!!.size - 1) {
            totalDistance += locationList!![i].distanceTo(locationList!![i + 1])
            totalTime += timeList!![i + 1] - timeList!![i]
            speedTotal += speedList!![i]
        }
        //return (totalDistance / (totalTime / 1000f))*(1000f/60f)
        return (totalTime/1000f)/3600f
    }
    private fun calculateDistance(): Float {
        if (locationList!!.size < 2) {
            return 0f
        }
        var totalDistance = 0f
        var totalTime: Long = 0
        var speedTotal = 0f
        for (i in 0 until locationList!!.size - 1) {
            totalDistance += locationList!![i].distanceTo(locationList!![i + 1])
            totalTime += timeList!![i + 1] - timeList!![i]
            speedTotal += speedList!![i]
        }
        //return (totalDistance / (totalTime / 1000f))*(1000f/60f)
        return totalDistance/1000f
    }

    private fun calculateInstSpeed(): Float {
        if (locationList!!.size < 2) {
            return 0f
        }
        var totalDistance = 0f
        var totalTime: Long = 0
        var last = locationList!!.size -1
        //for (i in 0 until locationList!!.size - 1) {
            totalDistance += locationList!![last-1].distanceTo(locationList!![last])
            totalTime += timeList!![last] - timeList!![last-1]
        //}
        //return (totalDistance / (totalTime / 1000f))*(1000f/60f)
        return speedList!![last]
    }

    override fun onPause() {
        super.onPause()
        locationManager!!.removeUpdates(locationListener!!)
    }

    override fun onResume() {
        super.onResume()
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                1
            )
        } else {
            locationManager!!.requestLocationUpdates(
                LocationManager.GPS_PROVIDER, 0, 0f,
                locationListener!!
            )
        }
    }
}
