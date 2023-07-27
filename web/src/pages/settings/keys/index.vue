<template>
    <CenterMain>
        <div class="flex justify-between items-center">
            <h1 class="text-lg font-semibold">Your keys:</h1>

            <div class="flex justify-between gap-2">
                <button class="inline-flex items-center bg-blue-500 hover:bg-blue-600 focus-visible:outline-blue-600 rounded-md px-3 py-2 text-sm font-semibold text-white shadow-sm focus-visible:outline focus-visible:outline-2 focus-visible:outline-offset-2 "
                        @click="importKey"
                >
                    <InboxArrowDownIcon aria-hidden="true" class="h-5 w-5 text-white mr-1"/>
                    <span>Import key</span>
                </button>

                <button class="inline-flex items-center bg-blue-500 hover:bg-blue-600 focus-visible:outline-blue-600 rounded-md px-3 py-2 text-sm font-semibold text-white shadow-sm focus-visible:outline focus-visible:outline-2 focus-visible:outline-offset-2 "
                        @click="generateKey"
                >
                    <KeyIcon aria-hidden="true" class="h-5 w-5 text-white mr-1"/>
                    <span>Generate key</span>
                </button>
            </div>

        </div>


        <ol class="divide-y divide-gray-100 list-decimal border rounded-2xl mt-2 px-2" role="list">
            <li v-for="key in keys" :key="key" class="flex items-center justify-between gap-x-6 py-4">
                <div class="min-w-0">
                    <div class="flex items-start gap-x-3">
                        <p class="text-base font-semibold leading-6 text-gray-900 overflow-x-scroll py-1 mx-2">{{ key.algorithm }}: <span class="text-base font-normal">{{ key.keyId.id }}</span></p>
                    </div>
                </div>
                <div class="flex flex-none items-center gap-x-4">
                    <NuxtLink :to="'/settings/keys/' + key.keyId.id"
                              class="hidden rounded-md bg-white px-2.5 py-1.5 text-sm font-semibold text-gray-900 shadow-sm ring-1 ring-inset ring-gray-300 hover:bg-gray-50 sm:block">
                        View key
                    </NuxtLink>
                </div>
            </li>
        </ol>
        <p v-if="keys && keys.length == 0" class="mt-2">No keys.</p>
    </CenterMain>
</template>

<script lang="ts" setup>
import CenterMain from "~/components/CenterMain.vue";
import {KeyIcon, InboxArrowDownIcon} from '@heroicons/vue/24/outline'

const keys = await useLazyFetch("/r/wallet/keys").data
refreshNuxtData()

function generateKey() {
    navigateTo("keys/generate")
}

function importKey() {
    navigateTo("keys/import")
}

useHead({
    title: "Keys - walt.id"
})
</script>

<style scoped>

</style>
