<template>
    <div>
        <PageHeader>
            <template v-slot:title>
                <div class="ml-3">
                    <h1 class="text-2xl font-bold leading-7 text-gray-900 sm:truncate sm:leading-9">
                        Present </h1>
                    <p>requested by <span class="underline">{{ verifierHost }}</span></p>
                </div>
            </template>

            <template v-if="!immediateAccept" v-slot:menu>
                <ActionButton icon="heroicons:x-mark" class="inline-flex focus:outline focus:outline-red-700 focus:outline-offset-2 items-center rounded-md bg-red-600 px-3 py-2 text-sm font-semibold text-white shadow-sm hover:bg-red-700 hover:scale-105 hover:animate-pulse focus:animate-none"
                              display-text="Reject"
                              type="button" @click="navigateTo('/')"/>

                <ActionButton icon="heroicons:check" :failed="failed" class="inline-flex focus:outline focus:outline-offset-2 items-center rounded-md px-3 py-2 text-sm font-semibold text-white shadow-sm"
                              :class="[failed ? 'bg-red-600 animate-pulse focus:outline focus:outline-red-700 focus:outline-offset-2 hover:bg-red-700 hover:scale-105' : 'bg-green-600 focus:outline-green-700 hover:bg-green-700 hover:scale-105 hover:animate-pulse focus:animate-none']"
                              display-text="Accept"
                              type="button" @click="acceptPresentation"/>
            </template>
        </PageHeader>
        <CenterMain>
            <h1 class="text-2xl font-semibold mb-2">Presentation</h1>

            <LoadingIndicator v-if="immediateAccept" class="my-6 mb-12 w-full">
                Presenting credential(s)...
            </LoadingIndicator>

            <p class="mb-1">The following credentials will be presented:</p>

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
import {groupBy} from "~/composables/groupings";

const query = useRoute().query

const request = atob(query.request)
console.log("Decoded request: " + request)

const presentationUrl = new URL(request)
const presentationParams = presentationUrl.searchParams

const verifierHost = new URL(presentationParams.get("redirect_uri") ?? "").host

const presentationDefinition = presentationParams.get("presentation_definition")
let inputDescriptors = JSON.parse(presentationDefinition)["input_descriptors"]

let groupedCredentialTypes = groupBy(inputDescriptors.map(item => {
    return {id: item.id, name: item.constraints.fields[0].filter.const}
}), c => c.name)

const immediateAccept = ref(false)

const failed = ref(false)

async function acceptPresentation() {
    try {
        const response = await $fetch<{ redirectUri: string | null }>("/r/wallet/exchange/usePresentationRequest", {
            method: 'POST',
            body: request
        })

        const redirect = response.redirectUri

        if (redirect != null) {
            navigateTo(redirect, {
                external: true
            })
        } else {
            navigateTo('/')
        }
    } catch (e) {
        failed.value = true
        throw e
    }
}


if (query.accept) { // TODO make accept a JWT or something wallet-backend secured
    immediateAccept.value = true
    acceptPresentation()
}
</script>

<style scoped>

</style>
