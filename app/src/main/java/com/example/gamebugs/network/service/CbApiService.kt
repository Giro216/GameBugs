package com.example.gamebugs.network.service

import com.example.gamebugs.network.model.MetalCurrencyResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface CbApiService {
    @GET("scripts/xml_metall.asp")
    suspend fun getGoldPrices(
        @Query("data_req1") dataReq1: String,
        @Query("data_req2") dataReq2: String
    ): MetalCurrencyResponse
}