package id.walt.service

import kotlinx.serialization.Serializable

@Serializable
data class Did(
    val did : String,
    val alias : String = "n/a",
    val default : Boolean = false,
    val document: String,
)
