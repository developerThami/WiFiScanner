package com.aruba.wifirssiobserver

import com.aruba.wifirssiobserver.network.NetworkFactory
import com.aruba.wifirssiobserver.network.api.WifiNetworkInfoApi
import com.aruba.wifirssiobserver.network.request.WifiNetworkInfoRequestBody
import com.aruba.wifirssiobserver.network.response.WifiNetworkInfoResponse
import com.squareup.moshi.Moshi
import okhttp3.HttpUrl
import okhttp3.mockwebserver.Dispatcher
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okhttp3.mockwebserver.RecordedRequest
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class NetworkFactoryTest {

    private val testResponse =
        "{\"status\":\"success\", \"message\":\"Wifi information update received successfully\"}"
    private val testRequestBody =
        "{\"wifi_networks\":[{\"rssi\":\"-35\",\"ssid\":\"Phoenix\"},{\"rssi\":\"-39\",\"ssid\":\"Phoenix_X\"},{\"rssi\":\"-68\",\"ssid\":\"Van Der Linde_Wireless\"},{\"rssi\":\"-89\",\"ssid\":\"Van Der Linde_Wireless_5G\"},{\"rssi\":\"-51\",\"ssid\":\"Phoenix_Guest\"}]}"


    private lateinit var mockWebServer: MockWebServer
    private lateinit var testNetworkFactory: NetworkFactory
    private lateinit var testWifiNetworkInfoApi: WifiNetworkInfoApi

    @Before
    @Throws(Exception::class)
    fun setUp() {

        mockWebServer = MockWebServer()
        mockWebServer.start()
        val dispatcher: Dispatcher = object : Dispatcher() {
            @Throws(InterruptedException::class)
            override fun dispatch(request: RecordedRequest): MockResponse {
                when (request.path) {
                    "/wifi_environments" -> return MockResponse().setResponseCode(200)
                        .setBody(testResponse)
                }
                return MockResponse().setResponseCode(404)
            }
        }
        mockWebServer.dispatcher = dispatcher

        val baseUrl: HttpUrl = mockWebServer.url("/")

        testNetworkFactory = NetworkFactory()
        testWifiNetworkInfoApi =
            testNetworkFactory.createApi(
                WifiNetworkInfoApi::class.java,
                baseUrl = baseUrl.toString()
            )

    }

    @Test
    fun testPostWiFiRssiInfo() {

        val moshi = Moshi.Builder().build()
        val wifiNetworkInfoRequestBody =
            moshi.adapter(WifiNetworkInfoRequestBody::class.java).fromJson(testRequestBody)

        val response =
            testWifiNetworkInfoApi.postWiFiRssiInfo(wifiNetworkInfoRequestBody!!).execute()

        mockWebServer.takeRequest()
    }
}
