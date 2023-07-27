package id.walt.web.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic

@Serializable
sealed class LoginRequest

@Serializable
@SerialName("email")
data class EmailLoginRequest(val username: String, val password: String) : LoginRequest()

@Serializable
@SerialName("address")
data class AddressLoginRequest(val address: String, val ecosystem: String) : LoginRequest()

val module = SerializersModule {
    this.polymorphic(LoginRequest::class) {
        EmailLoginRequest::class
        AddressLoginRequest::class
    }
}

val LoginRequestJson = Json {
    serializersModule = module
    ignoreUnknownKeys = true
}
