package com.example.noticias

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface TimesWireService {
    @GET("all/all.json")
    fun getLatestNews(
        @Query("api-key") apiKey: String
    ): Call<NYTResponse>
}
