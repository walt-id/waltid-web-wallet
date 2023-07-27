<template>
    <div>
        <PageHeader>
            <template v-slot:title>
                <div class="ml-3">
                    <h1 class="text-2xl font-bold leading-7 text-gray-900 sm:truncate sm:leading-9">
                        Receive {{ credentialCount === 1 ? 'single' : credentialCount }} {{ credentialCount === 1 ? 'credential' : 'credentials' }}</h1>
                    <p>issued by <span class="underline">{{ issuerHost }}</span></p>
                </div>
            </template>

            <template v-if="!immediateAccept" v-slot:menu>
                <ActionButton :icon="XMarkIcon" class="inline-flex focus:outline focus:outline-red-700 focus:outline-offset-2 items-center rounded-md bg-red-600 px-3 py-2 text-sm font-semibold text-white shadow-sm hover:bg-red-700 hover:scale-105 hover:animate-pulse focus:animate-none"
                              display-text="Reject"
                              type="button" @click="navigateTo('/')"/>

                <ActionButton :icon="CheckIcon" class="inline-flex focus:outline focus:outline-green-700 focus:outline-offset-2 items-center rounded-md bg-green-600 px-3 py-2 text-sm font-semibold text-white shadow-sm hover:bg-green-700 hover:scale-105 hover:animate-pulse focus:animate-none"
                              display-text="Accept"
                              type="button" @click="acceptCredential"/>
            </template>
        </PageHeader>

        <CenterMain>
            <h1 class="text-2xl font-semibold mb-2">Issuance</h1>

            <LoadingIndicator v-if="immediateAccept" class="my-6 mb-12 w-full">
                Receiving {{ credentialCount }} credential(s)...
            </LoadingIndicator>

            <p class="mb-1">The following credentials will be issued:</p>

            <div aria-label="Credential list" class="h-full overflow-y-auto shadow-xl">
                <div v-for="group in groupedCredentialTypes.keys()" :key="group.id" class="relative">
                    <div class="sticky top-0 z-10 border-y border-b-gray-200 border-t-gray-100 bg-gray-50 px-3 py-1.5 text-sm font-semibold leading-6 text-gray-900">
                        <h3>{{ group }}s:</h3>
                    </div>
                    <ul class="divide-y divide-gray-100" role="list">
                        <li v-for="credential in groupedCredentialTypes.get(group)" :key="credential" class="flex gap-x-4 px-3 py-5">

                            <CredentialIcon :credentialType="credential.name" class="h-6 w-6 flex-none rounded-full bg-gray-50"></CredentialIcon>

                            <div class="min-w-0 flex flex-row items-center">
                                <span class="text-lg font-semibold leading-6 text-gray-900">{{ credential.id }}.</span> <span
                                class="ml-1 truncate text-sm leading-5 text-gray-800">{{ credential.name }}</span>
                            </div>
                        </li>
                    </ul>
                </div>
            </div>
        </CenterMain>
    </div>
</template>

<script lang="ts" setup>
import CenterMain from "~/components/CenterMain.vue";
import {CheckIcon, XMarkIcon} from '@heroicons/vue/24/outline'
import PageHeader from "~/components/PageHeader.vue";
import CredentialIcon from "~/components/CredentialIcon.vue";
import ActionButton from "~/components/buttons/ActionButton.vue";
import LoadingIndicator from "~/components/loading/LoadingIndicator.vue";

const query = useRoute().query

const request = atob(query.request)

const immediateAccept = ref(false)
const issuanceUrl = new URL(request)
const issuanceParams = issuanceUrl.searchParams

const issuerHost = new URL(issuanceParams.get("issuer") ?? "").host
const credentialTypes = issuanceParams.getAll("credential_type")

const credentialCount = credentialTypes.length

let i = 0
const groupedCredentialTypes = groupBy(credentialTypes.map(item => {
    return {id: ++i, name: item}
}), c => c.name)

async function acceptCredential() {
    await $fetch("/r/wallet/exchange/useOfferRequest", {
        method: 'POST',
        body: request
    })
    navigateTo('/')
}

if (query.accept) { // TODO make accept a JWT or something wallet-backend secured
    immediateAccept.value = true
    acceptCredential()
}

/*if (query.request) {
    const request = atob(query.request)
    console.log(request)
} else {
    console.error("No request")
}*/
</script>

<style scoped>

</style>
