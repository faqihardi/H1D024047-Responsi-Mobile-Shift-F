package com.faqih.videogame.data.repository

import com.faqih.videogame.data.model.Game
import com.faqih.videogame.util.VideoGameConstants
import com.faqih.videogame.network.ApiClient
import com.faqih.videogame.network.ApiInterface

class GameRepository(
    private val apiService: ApiInterface
) {
    suspend fun getGames(search: String? = null): List<Game> {
        return apiService.getGames(
            apiKey = VideoGameConstants.API_KEY,
            search = search
        ).results
    }

    suspend fun getGameDetail(gameId: Int): Game {
        return apiService.getGameDetail(
            gameId = gameId,
            apiKey = VideoGameConstants.API_KEY
        )
    }

    companion object {

        val instance: GameRepository by lazy {
            GameRepository(ApiClient.instance)
        }
    }
}