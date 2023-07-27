<template>
    <CenterMain>
        <BackButton/>
        <LoadingIndicator v-if="pending">Loading DID...</LoadingIndicator>
        <div v-else>
            <div class="flex space-x-3">
                <div class="min-w-0 flex-1">
                    <p class="text-sm font-semibold text-gray-900 whitespace-pre-wrap">
                        DID: {{ didId }} </p>
                    <p class="text-sm text-gray-500">
                        DID document data below:</p>
                </div>
            </div>
            <div v-if="didDoc">
                <div class="p-3 shadow mt-3">
                    <h3 class="font-semibold mb-2">QR code</h3>
                    <qrcode-vue v-if="didDoc && JSON.stringify(didDoc).length <= 4296" :value="JSON.stringify(didDoc)" level="L" size="300"></qrcode-vue>
                    <p v-else-if="didDoc && JSON.stringify(didDoc).length">Unfortunately, this DID document is too big to be viewable as QR code (DID document size is {{ didDoc.length }} characters, but the
                        maximum a QR code can hold is 4296). {{ JSON.stringify(didDoc).length }}</p>
                    <p v-else>No DID document could be loaded!</p>
                </div>
                <div class="shadow p-3 mt-2 font-mono overflow-scroll">
                    <h3 class="font-semibold mb-2">JSON</h3>
                    <pre>{{ didDoc ? JSON.stringify(didDoc, null, 2) : "No DID" }}</pre>
                </div>
            </div>
            <div v-else class="p-3 shadow mt-3 bg-red-200 border-red-300 border" v-if="!pending">
                <h3 class="font-semibold text-red-500">The DID could not be loaded.</h3>
            </div>
        </div>
    </CenterMain>
</template>

<script lang="ts" setup>
import LoadingIndicator from "~/components/loading/LoadingIndicator.vue";
import QrcodeVue from 'qrcode.vue'
import CenterMain from "~/components/CenterMain.vue";
import BackButton from "~/components/buttons/BackButton.vue";

const route = useRoute()

const didId = route.params.didId

const {data: didDoc, pending, refresh, error} = await useLazyFetch(`/r/wallet/dids/${didId}`)
refreshNuxtData()

useHead({
    title: "View DID - walt.id"
})
</script>

<style scoped>

</style>
