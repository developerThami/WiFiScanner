package com.aruba.wifirssiobserver.ui

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.net.wifi.WifiManager
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.aruba.wifirssiobserver.R
import com.aruba.wifirssiobserver.network.NetworkFactory
import com.aruba.wifirssiobserver.network.api.WifiNetworkInfoApi
import com.aruba.wifirssiobserver.network.request.WifiNetworkInfo
import com.aruba.wifirssiobserver.network.request.WifiNetworkInfoRequestBody
import com.aruba.wifirssiobserver.network.response.WifiNetworkInfoResponse
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.concurrent.Executors


class MainActivity : AppCompatActivity() {

    private val PERMISSION_REQUEST: Int = 9009
    private val intentFilter = IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)
    private lateinit var wifiManager: WifiManager

    private var wifiNetworks: MutableList<WifiNetworkInfo> = arrayListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        wifiManager = applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager

        val selfPermission =
            ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
        if (selfPermission != PackageManager.PERMISSION_GRANTED) {
            requestLocationPermission()
        }

        scan_btn.setOnClickListener {
            scanForAvailableWifiNetworks()
        }

    }

    private val wifiReceiver = object : BroadcastReceiver() {

        override fun onReceive(context: Context?, intent: Intent?) {

            unregisterReceiver()

            val wifiResults = wifiManager.scanResults

            if (wifiResults != null) {

                toggleStartButtonVisibility(View.VISIBLE)
                toggleProgressIndicatorVisibility(View.GONE)

                for (scanResult in wifiResults) {

                    val ssid = scanResult.SSID
                    val level = scanResult.level

                    val networkInfo = WifiNetworkInfo(ssid, level.toString())

                    wifiNetworks.add(networkInfo)
                }

                reportWifiScanResults(wifiNetworks)
            }

        }
    }

    private fun reportWifiScanResults(wifiNetworks: MutableList<WifiNetworkInfo>) {

        val baseUrl = "https://imaginary-api.net/"

        val wifiRssiApi = NetworkFactory().createApi(WifiNetworkInfoApi::class.java, baseUrl)

        val dispatcher = Executors.newSingleThreadExecutor().asCoroutineDispatcher()

        CoroutineScope(dispatcher).launch {

            val requestBody = WifiNetworkInfoRequestBody(wifiNetworks)

            val request = wifiRssiApi.postWiFiRssiInfo(requestBody)

            val callback = object : Callback<WifiNetworkInfoResponse?> {
                override fun onFailure(request: Call<WifiNetworkInfoResponse?>, t: Throwable) {
                    showMessage(getString(R.string.failure_message), android.R.color.holo_red_dark)
                }

                override fun onResponse(
                    request: Call<WifiNetworkInfoResponse?>,
                    response: Response<WifiNetworkInfoResponse?>
                ) {
                    showMessage(
                        getString(R.string.success_message),
                        android.R.color.holo_green_dark
                    )
                }

            }

            request.enqueue(callback)
        }

    }

    fun showMessage(message: String, color: Int) {
        val snackbar = Snackbar.make(scan_btn, message, Snackbar.LENGTH_LONG)
        snackbar.view.setBackgroundColor(ContextCompat.getColor(this, color))
        snackbar.show()
    }

    private fun toggleProgressIndicatorVisibility(visibility: Int) {
        progress_indicator.visibility = visibility
    }

    private fun toggleStartButtonVisibility(visibility: Int) {
        scan_btn.visibility = visibility
    }

    private fun scanForAvailableWifiNetworks() {

        toggleStartButtonVisibility(View.GONE)
        toggleProgressIndicatorVisibility(View.VISIBLE)

        if (!wifiManager.isWifiEnabled) {
            wifiManager.isWifiEnabled = true
        }

        registerReceiver()
        wifiManager.startScan()

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            PERMISSION_REQUEST -> {
                // If request is cancelled, the result arrays are empty.
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    scanForAvailableWifiNetworks()
                } else {
                    requestLocationPermission()
                }
                return
            }
        }
    }

    private fun requestLocationPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
            PERMISSION_REQUEST
        )
    }

    private fun registerReceiver() {
        registerReceiver(wifiReceiver, intentFilter)
    }

    private fun unregisterReceiver() {
        unregisterReceiver(wifiReceiver)
    }

    override fun onResume() {
        registerReceiver()
        super.onResume()
    }

    override fun onPause() {
        unregisterReceiver()
        super.onPause()
    }

}
