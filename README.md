# WiFiScanner
 Android application scans for available wifi networks and reports each network's RSSI to an imaginary API. 

## Overview of considerations

The WIFIScanner project utilizes the:

- **WifiManager** from Android's wifi to scan for avaiable wifi networks, the wifi RSSI and SSID are extracted form the ScanResult's received through a broadast receiver.

- **Kotlin Coroutines** for performing backround tasks and seamless threading.

- **Logging-Interceptor** for HTTP operations logging.

- **MockWebServer** for mocking web services for testing.

- **Moshi converter** for HTTP request and response serialization.

- **Retrofit 2** a HTTP client for handing the sending the HTTP calls.

<b>Built with</b>
- [Kotlin Coroutines](https://github.com/Kotlin/kotlinx.coroutines)
- [Retrofit 2](https://github.com/square/retrofit)
- [logging intercepter](https://github.com/square/okhttp/tree/master/okhttp-logging-interceptor)
- [Moshi converter](https://github.com/square/retrofit/tree/master/retrofit-converters/moshi)
- [MockWebServer](https://github.com/square/okhttp/tree/master/mockwebserver)
