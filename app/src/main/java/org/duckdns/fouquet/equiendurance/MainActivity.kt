package org.duckdns.fouquet.equiendurance

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat


class MainActivity : AppCompatActivity() {
    private var textViewSpeed: TextView? = null
    private var locationManager: LocationManager? = null
    private var locationListener: LocationListener? = null
    private var locationList: MutableList<Location>? = null
    private var timeList: MutableList<Long>? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        textViewSpeed = findViewById(R.id.text_view_speed)
        locationManager = getSystemService(LOCATION_SERVICE) as LocationManager
        locationList = ArrayList()
        timeList = ArrayList()
        locationListener = object : LocationListener {
            override fun onLocationChanged(location: Location) {
                (locationList as ArrayList<Location>).add(location)
                (timeList as ArrayList<Long>).add(System.currentTimeMillis())
                val speed = calculateAverageSpeed()
                (textViewSpeed as TextView).text =
                    "Vitesse moyenne : " + String.format("%.2f", speed) + " km/h"
            }

            override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {}
            override fun onProviderEnabled(provider: String) {}
            override fun onProviderDisabled(provider: String) {}
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
        for (i in 0 until locationList!!.size - 1) {
            totalDistance += locationList!![i].distanceTo(locationList!![i + 1])
            totalTime += timeList!![i + 1] - timeList!![i]
        }
        return (totalDistance / (totalTime / 1000f))*(1000f/60f)
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
