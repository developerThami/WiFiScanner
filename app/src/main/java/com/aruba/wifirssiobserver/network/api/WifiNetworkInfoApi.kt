package com.aruba.wifirssiobserver.network.api

import com.aruba.wifirssiobserver.network.request.WifiNetworkInfoRequestBody
import com.aruba.wifirssiobserver.network.response.WifiNetworkInfoResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

internal interface WifiNetworkInfoApi {

    @Headers("Content-Type: application/json")
    @POST("/wifi_environments")
    fun postWiFiRssiInfo(@Body requestBody: WifiNetworkInfoRequestBody): Call<WifiNetworkInfoResponse?>
}
