package com.nitish.myweatherapp.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.util.Log
import androidx.annotation.WorkerThread
import java.io.IOException
import java.net.InetSocketAddress
import java.net.Socket

object ConnectivityUtils {

    fun isConnectedToInternet(context: Context): Boolean {
        val connectivityManager: ConnectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork
        val connection = connectivityManager.getNetworkCapabilities(network)

        val connected = connection != null && (
            connection.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                connection.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)
            )
        if (connected) {
            return pingGoogleToConfirmInternetAvailable()
        }
        return connected
    }

    @WorkerThread
    private fun pingGoogleToConfirmInternetAvailable(): Boolean {
        return try {
            val timeoutMs = 500
            val sock = Socket()
            val socketAddress = InetSocketAddress("8.8.8.8", 53)
            sock.connect(socketAddress, timeoutMs)
            sock.close()
            true
        } catch (e: IOException) {
            Log.e("NetworkError", e.message?: "error")
            false
        }
    }
}