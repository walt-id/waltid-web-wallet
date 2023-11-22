import { decodeBase64ToUtf8 } from "~/composables/base64";

export function parseDisclosures(disclosureString: string) {
    try {
        return disclosureString.split("~").map((elem) => JSON.parse(decodeBase64ToUtf8(elem)));
    } catch (e) {
        console.error("Error parsing disclosures:", e);
        return [];
    }
}
