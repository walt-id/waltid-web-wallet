<template>
    <CenterMain>
        <ol class="divide-y divide-gray-100 list-decimal border rounded-2xl mt-2 px-3" role="list">
            <li v-for="method in methods" :key="method[0]" class="flex items-center justify-between gap-x-6 py-4">
                <div class="min-w-0">
                    <div class="flex items-start gap-x-3">
                        <p class="text-base font-semibold leading-6 text-gray-900">
                            {{ method[0] }}
                        </p>
                    </div>
                </div>
                <div class="flex flex-none items-center gap-x-4">
                    <button class="w-32 text-center rounded-md bg-white px-2.5 py-1.5 text-sm font-semibold text-gray-900 shadow-sm ring-1 ring-inset ring-gray-300 hover:bg-gray-50 sm:block">
                        Generate {{ method[0]}}
                    </button>
                </div>
            </li>
        </ol>
    </CenterMain>
</template>

<script setup lang="ts">
import CenterMain from "~/components/CenterMain.vue";

const methods = new Map([
    ["EdDSA_Ed25519", []],
    ["ECDSA_Secp256k1", []]
])


useHead({
    title: "Generate key - walt.id"
})
</script>

<style scoped>

</style>
