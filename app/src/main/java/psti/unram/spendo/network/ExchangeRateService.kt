package psti.unram.spendo.network

import retrofit2.http.GET
import retrofit2.http.Path

interface ExchangeRateService {
    @GET("{apiKey}/latest/USD")
    suspend fun getLatestRates(@Path("apiKey") apiKey: String): ExchangeRateResponse
}

data class ExchangeRateResponse(
    val result: String,
    val base_code: String,
    val conversion_rates: Map<String, Double>? // Ubah dari 'rates' ke 'conversion_rates'
)