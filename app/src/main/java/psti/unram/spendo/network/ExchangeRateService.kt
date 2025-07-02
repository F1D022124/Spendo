package psti.unram.spendo.network

import retrofit2.http.GET
import retrofit2.http.Path

interface ExchangeRateService {
    @GET("latest/{apiKey}")
    suspend fun getLatestRates(@Path("apiKey") apiKey: String): ExchangeRateResponse
}

data class ExchangeRateResponse(val rates: Map<String, Double>)