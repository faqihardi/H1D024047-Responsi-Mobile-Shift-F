package com.faqih.videogame.data.model

data class Game(
    val id: Int,
    val name: String?,
    val rating: Double?,
    val released: String?,
    val metacritic: Int?,
    val description_raw: String?,
    val genres: List<Genre>?,
    val platforms: List<PlatformInfo>?
)

data class Genre(
    val id: Int?,
    val name: String?
)

data class  PlatformInfo(
    val platform: Platform?
)

data class Platform(
    val id: Int?,
    val name: String?
)