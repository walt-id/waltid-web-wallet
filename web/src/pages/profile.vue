<template>
    <CenterMain>
        <h1 class="font-semibold">Profile</h1>
        <p>Username: {{ user.username }}</p>
        <!-- add-account -->
        <div class="items-center">
            <div class="mt-4">
                <button
                    type="button"
                    class="inline-flex items-center rounded-md bg-blue-500 px-3 py-2 text-sm font-semibold text-white shadow-sm hover:bg-blue-600 focus-visible:outline focus-visible:outline-2 focus-visible:outline-offset-2 focus-visible:outline-blue-600"
                    @click="connectAccount">
                    <PlusIcon aria-hidden="true" class="-ml-0.5 mr-1.5 h-5 w-5"/>
                    Add wallet address
                </button>
            </div>
        </div>
        <LoadingIndicator v-if="pending">Loading</LoadingIndicator>
        <span v-else-if="accounts && accounts.length > 0" class="font-semibold">Your connected addresses ({{ accounts.length }}):</span>
        <h3 v-else class="mt-2 text-sm font-semibold text-gray-900">No connected addresses</h3>
        <!-- account-list -->
        <ul class="divide-y divide-gray-100 list-decimal border rounded-2xl mt-2 px-2" role="list">
            <li v-for="account in accounts" :key="account.id" class="flex items-center justify-between gap-x-6 py-4">
                <div class="flex w-full">
                    <Web3WalletIcon :ecosystem="account.ecosystem" class="px-2"/>
                    <div class="flex truncate w-full">
                        <span class="text-base font-normal">{{ account.address }}</span>
                    </div>
                </div>
            </li>
        </ul>
    </CenterMain>
</template>

<script setup>
import CenterMain from "~/components/CenterMain.vue";
import {PlusIcon} from "@heroicons/vue/20/solid";
import Web3WalletIcon from "~/components/Web3WalletIcon.vue";
import AddWalletModal from "~/components/modals/AddWalletModal.vue";
import useModalStore from "~/stores/useModalStore";
import { useUserStore } from "~/stores/user";
import { storeToRefs } from 'pinia'

const modalStore = useModalStore();
const userStore = useUserStore()
const { user } = storeToRefs(userStore)
const {data: accounts, pending, refresh, error} = await useLazyFetch("/r/wallet/accounts")

function connectAccount() {
    modalStore.openModal({
        component: AddWalletModal,
        props: {
            callback: (wallet) => {
                if(accounts.value.filter(function (e) { return e.id === wallet.id; }).length == 0){
                    accounts.value.push(wallet)
                }
            }
        }
    });
}
</script>

<style scoped></style>
