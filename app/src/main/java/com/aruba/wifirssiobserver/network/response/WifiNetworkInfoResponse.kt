package com.aruba.wifirssiobserver.network.response

import com.squareup.moshi.Json

class WifiNetworkInfoResponse(@field:Json(name = "status") var status: String?, @field:Json(name = "message") var message: String?)