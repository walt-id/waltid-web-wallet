package id.walt.web

import id.walt.db.models.AccountWalletPermissions
import io.ktor.http.*
import kotlinx.serialization.SerialName

sealed class WebException(val status: HttpStatusCode, message: String) : Exception(message)

class UnauthorizedException(message: String) : WebException(HttpStatusCode.Unauthorized, message)

@SerialName("InsufficientPermissions")
class InsufficientPermissionsException(
    minimumRequired: AccountWalletPermissions,
    current: AccountWalletPermissions,
) : WebException(HttpStatusCode.Unauthorized, "You do not have enough permissions to access this action. Minimum required permissions: $minimumRequired, your current permissions: $current")
