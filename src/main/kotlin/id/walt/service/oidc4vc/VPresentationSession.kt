package id.walt.service.oidc4vc

import id.walt.oid4vc.providers.SIOPSession
import id.walt.oid4vc.requests.AuthorizationRequest
import kotlinx.datetime.Instant

class VPresentationSession(
  id: String,
  authorizationRequest: AuthorizationRequest?,
  expirationTimestamp: Instant,
  val selectedCredentialIds: Set<String>
): SIOPSession(id, authorizationRequest, expirationTimestamp)
