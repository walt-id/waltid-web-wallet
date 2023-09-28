<template>
    <CenterMain>
        <BackButton/>
        <LoadingIndicator v-if="pending">Loading credential...</LoadingIndicator>
        <div v-else>
            <div class="flex space-x-3">
                <div class="min-w-0 flex-1">
                    <p class="text-sm font-semibold text-gray-900 whitespace-pre-wrap">
                        {{ credentialId }} </p>
                    <p class="text-sm text-gray-500">
                        Verifiable Credential data below:</p>
                </div>
            </div>
           <!-- <div class="p-3 shadow mt-3">
                <h3 class="font-semibold mb-2">QR code</h3>
                <div v-if="credential && credential.length">
                    <qrcode-vue v-if="credential.length <= 4296" :value="credential" level="L" size="300"></qrcode-vue>
                    <p v-else>Unfortunately, this Verifiable Credential is too big to be viewable as QR code (credential size is {{ credential.length }} characters, but the maximum a QR code
                        can hold is 4296).</p>
                </div>

            </div>-->
            <div class="shadow p-3 mt-2 font-mono overflow-scroll">
                <h3 class="font-semibold mb-2">JWT</h3>
                <pre v-if="credential && credential.length">{{ /*JSON.stringify(JSON.parse(*/credential/*), null, 2)*/ ?? "" }}</pre>
            </div>
            <div class="shadow p-3 mt-2 font-mono overflow-scroll">
                <h3 class="font-semibold mb-2">JSON</h3>
                <pre v-if="credential && credential.length">{{ jwtJson }} </pre>
            </div>
        </div>
    </CenterMain>
</template>

<script lang="ts" setup>
import LoadingIndicator from "~/components/loading/LoadingIndicator.vue";
import QrcodeVue from 'qrcode.vue'
import CenterMain from "~/components/CenterMain.vue";
import BackButton from "~/components/buttons/BackButton.vue";
import {btoa} from "buffer";

const route = useRoute()

const credentialId = route.params.credentialId

const jwtJson = computed(() => {
    console.log("Cred: " + credential.value)
    if (credential.value) {
        return JSON.parse(atob(credential.value.split(".")[1]).toString())
    } else return ""
})

const {data: credential, pending, refresh, error} = await useLazyFetch(`/r/wallet/credentials/${credentialId}`)
refreshNuxtData()

useHead({
    title: "View credential - walt.id"
})
</script>

<style scoped>

</style>
