package id.walt.service

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject

@Serializable
data class Credential(
  val id: String,
  val parsedCredential: JsonObject,
  val rawCredential: String
)
