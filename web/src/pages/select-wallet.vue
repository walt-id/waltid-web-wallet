<template>
    <CenterMain>
        <div class="flex justify-between items-center">
            <h1 class="text-lg font-semibold">Select wallet:</h1>

            <div class="flex justify-between gap-2">
                <button
                    class="inline-flex items-center bg-blue-500 hover:bg-blue-600 focus-visible:outline-blue-600 rounded-md px-3 py-2 text-sm font-semibold text-white shadow-sm focus-visible:outline focus-visible:outline-2 focus-visible:outline-offset-2"
                    @click="createWallet"
                >
                    <InboxArrowDownIcon aria-hidden="true" class="h-5 w-5 text-white mr-1" />
                    <span>Create new wallet</span>
                </button>
            </div>
        </div>

        <ol class="divide-y divide-gray-100 list-decimal border rounded-2xl mt-2 px-2" role="list">
            <li v-for="wallet in wallets" :key="wallet.id" class="flex items-center justify-between gap-x-6 py-4">

                <div class="min-w-0 flex items-center">

                    <div>
                        <div class="pl-1">
                            <Icon class="h-7 w-7" name="heroicons:wallet" />
                        </div>
                    </div>

                    <div class="min-w-0">
                        <div class="flex items-start gap-x-3">
                            <p class="mx-2 text-base font-semibold leading-6 text-gray-900">
                                {{ wallet.name }}
                            </p>
                        </div>
                        <div class="flex items-start gap-x-3">
                            <p class="mx-2 overflow-x-auto text-base font-normal leading-6 text-gray-500">
                                {{ wallet.createdOn }}
                            </p>
                        </div>
                    </div>
                </div>
                <div class="flex flex-none items-center gap-x-4">
                    <NuxtLink
                        :to="'/' + wallet.id"
                        class="hidden rounded-md bg-white px-3 py-2 text-base font-semibold text-gray-900 shadow-sm ring-1 ring-inset ring-gray-300 hover:bg-gray-50 sm:block"
                    >
                        Select wallet
                        <Icon class="h-5 w-5" name="heroicons:chevron-right" />
                    </NuxtLink>
                </div>
            </li>
        </ol>
        <p v-if="wallets && wallets.length == 0" class="mt-2">No wallets.</p>
    </CenterMain>
</template>

<script lang="ts" setup>

import { InboxArrowDownIcon } from "@heroicons/vue/24/outline";
import CenterMain from "~/components/CenterMain.vue";

const wallets = [{
    name: "Wallet of X",
    id: "xabc",
    createdOn: "2023-11-15"
}];
</script>

<style scoped>

</style>
