export enum SiopRequestType {
    PRESENTATION,
    ISSUANCE
}

const siopPrefixMapping: Map<string, SiopRequestType> = new Map([
    ["openid://", SiopRequestType.PRESENTATION],
    ["openid4vp://", SiopRequestType.PRESENTATION],
    ["openid-initiate-issuance://", SiopRequestType.ISSUANCE],
    ["openid-credential-offer://", SiopRequestType.ISSUANCE],
])

export function fixRequest(req: string) {
    return req.replaceAll("\n", "").trim()
}

export function encodeRequest(req: string) {
    return btoa(req).replaceAll('=', "")
}

export function isSiopRequest(req: string): boolean {
    return getSiopRequestType(fixRequest(req)) != null
}

export function getSiopRequestType(req: string): SiopRequestType | null {
    req = fixRequest(req)

    for (let [key, value] of siopPrefixMapping) {
        if (req.startsWith(key)) return value
    }
    return null
}
