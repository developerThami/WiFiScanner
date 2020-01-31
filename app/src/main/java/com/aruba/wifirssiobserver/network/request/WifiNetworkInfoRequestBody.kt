package com.aruba.wifirssiobserver.network.request

import com.squareup.moshi.Json

class WifiNetworkInfoRequestBody(@field:Json(name = "wifi_networks") var wifiNetworks: MutableList<WifiNetworkInfo>)