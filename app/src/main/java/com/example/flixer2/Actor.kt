package com.example.flixer2

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ActorResponse(
    @SerialName("results")
    val results: List<Actor>?
)

@Serializable
data class Actor(
    @SerialName("id")
    val id: Int,
    @SerialName("name")
    val name: String?,
    @SerialName("profile_path")
    val profilePath: String?
) : java.io.Serializable {
    val profileUrl: String?
        get() = profilePath?.let { "https://image.tmdb.org/t/p/w500/$it" }
}
