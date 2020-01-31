package com.aruba.wifirssiobserver.network.request

import com.squareup.moshi.Json

class WifiNetworkInfo(@field:Json(name = "ssid") var ssid: String?, @field:Json(name = "rssi") var rssi: String?)
