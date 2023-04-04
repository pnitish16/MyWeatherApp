package com.nitish.myweatherapp.utils

import android.annotation.SuppressLint
import android.app.Activity
import android.content.IntentSender
import android.location.Location
import android.os.Looper
import android.util.Log
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*

/**
 * [Note: GPS permission needs to be handled by the caller(Activity, Fragment)]
 * @param isContinuesUpdateRequired :
 * If set TRUE "onLocationUpdate" function will be called continuously
 * If set FALSE "onLocationUpdate" function will be called once and "locationUpdate" will be removed from "locationProviderClient"
 *
 * @see stopLocationUpdate():
 * If location is requested for one time use then no need to worry about calling this method as once the location is sent we are removing "locationUpdate" from "locationProviderClient"
 * If location is requested for continues update then we should call "stopLocationUpdate" when we switch from screen(activity, fragment)[although this depends on the scenario]
 **/
@SuppressLint("MissingPermission")
class GPSLocationHelper(
    private val activity: Activity,
    private val isContinuesUpdateRequired: Boolean,
    private val onLocationUpdate: (location: Location?) -> Unit
) {

    private lateinit var locationProviderClient: FusedLocationProviderClient

    private var locationUpdate: LocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            Log.d("GPSLocationHelper","onLocationResult " + locationResult.lastLocation?.latitude)
            if (!isContinuesUpdateRequired) {
                // If location updated are not required continuously then we need to remove location update

                // "locationProviderClient" wont be null or uninitialized when this line will execute, but added a safer side check
                if (this@GPSLocationHelper::locationProviderClient.isInitialized) {
                    locationProviderClient.removeLocationUpdates(this)
                }
            }
            onLocationUpdate.invoke(locationResult.lastLocation)
        }
    }

    companion object {
        const val UPDATE_INTERVAL: Long = 10 * 1000 // 10 secs
        const val FASTEST_INTERVAL: Long = 2000 // 2 sec
        const val REQUEST_CHECK_SETTINGS = 100
    }

    init {
        provideLastLocationElseStartLocationUpdate()
    }

    private fun provideLastLocationElseStartLocationUpdate() {
        if (!isContinuesUpdateRequired) {
            // If location is requested for one time only then in this case we can return the "lastLocation" only
            // Most of the time this will be available in case it returns null then only we will start location update
            LocationServices.getFusedLocationProviderClient(activity).lastLocation
                .addOnSuccessListener {
                    if (it != null) {
                        onLocationUpdate.invoke(it)
                    } else {
                        startLocationUpdates()
                    }
                }
        } else {
            startLocationUpdates()
        }
    }

    private fun startLocationUpdates() {
        // Create the location request to start receiving updates
        val mLocationRequestHighAccuracy = LocationRequest.create().apply {
            interval = UPDATE_INTERVAL
            fastestInterval = FASTEST_INTERVAL
            priority = Priority.PRIORITY_HIGH_ACCURACY
        }

        // Create LocationSettingsRequest object using location request
        val locationSettingsRequest = LocationSettingsRequest.Builder()
            .addLocationRequest(mLocationRequestHighAccuracy)
            .build()

        // Check whether location settings are satisfied
        // https://developers.google.com/android/reference/com/google/android/gms/location/SettingsClient
        val task = LocationServices.getSettingsClient(activity)
            .checkLocationSettings(locationSettingsRequest)

        task.addOnSuccessListener {
            // All location settings are satisfied
            // We can initialize location requests here.

            locationProviderClient = LocationServices.getFusedLocationProviderClient(activity)
            locationProviderClient.requestLocationUpdates(
                mLocationRequestHighAccuracy,
                locationUpdate,
                Looper.myLooper()!!
            )
        }
        /*
        * https://developers.google.com/android/reference/com/google/android/gms/location/SettingsClient
        * */
        task.addOnFailureListener {
            Log.e("error", it.message +  "GPSLocationHelper catch")

            if (it is ApiException) {
                when (it.statusCode) {
                    LocationSettingsStatusCodes.RESOLUTION_REQUIRED -> {
                        // Location settings are not satisfied. But could be fixed by showing the user a dialog.
                        try {
                            // Cast to a resolvable exception.
                            val resolvable = it as ResolvableApiException
                            // Show the dialog by calling startResolutionForResult(),
                            // and check the result in onActivityResult().
                            resolvable.startResolutionForResult(
                                activity,
                                REQUEST_CHECK_SETTINGS
                            )
                        } catch (e: IntentSender.SendIntentException) {
                            // Ignore the error.
                        } catch (e: ClassCastException) {
                            // Ignore, should be an impossible error.
                        }
                    }
                    LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE -> {
                        // Location settings are not satisfied.
                        // However, we have no way to fix the settings so we won't show the dialog.
                    }
                }
            }
            onLocationUpdate.invoke(null)
        }
    }

    /*
    * If location is requested for one time use then no need to worry about calling this method as once the location is sent we are removing "locationUpdate" from "locationProviderClient"
    * If location is requested for continues update then we should call "stopLocationUpdate" when we switch from screen(activity, fragment)[although this depends on the scenario]
    * */
    fun stopLocationUpdate() {
        if (this@GPSLocationHelper::locationProviderClient.isInitialized) {
            locationProviderClient.removeLocationUpdates(locationUpdate)
        }
    }
}
