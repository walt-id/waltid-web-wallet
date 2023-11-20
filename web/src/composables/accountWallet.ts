import { $fetch } from "ofetch";

export type WalletListing = {
    id: string,
    name: string
    createdOn: string
    addedOn: string
    permission: string
}

export type WalletListings = {
    account: string,
    wallets: WalletListing[]
}

export async function listWallets() {
    const {data} = await useFetch<WalletListings>("/r/wallet/accounts/wallets")
    return data
}
