package psti.unram.spendo.network

import retrofit2.http.GET
import retrofit2.http.Query

interface ExchangeRateService {
    @GET("latest/IDR")
    suspend fun getLatestRates(@Query("access_key") apiKey: String): ExchangeRateResponse
}

data class ExchangeRateResponse(val rates: Map<String, Double>)

//data class Quadruple<out A, out B, out C, out D>(
//    val first: A,
//    val second: B,
//    val third: C,
//    val fourth: D?
//)