package com.faqih.videogame.network

import com.faqih.videogame.data.model.Game
import com.faqih.videogame.data.model.GameResponse
import com.faqih.videogame.util.VideoGameConstants
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiInterface {

    @GET("games")
    suspend fun getGames(
        @Query("key") apiKey: String,
        @Query("search") search: String?,
        @Query("page_size") pageSize: Int = 20
    ): GameResponse

    @GET("games/{id}")
    suspend fun getGameDetail(
        @Path("id") gameId: Int,
        @Query("key") apiKey: String
    ): Game
}

object ApiClient {

    val instance: ApiInterface by lazy {
        Retrofit.Builder()
            .baseUrl(VideoGameConstants.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiInterface::class.java)
    }
}