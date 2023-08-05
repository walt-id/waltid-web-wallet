<template>
    <div>
        <CloseButton class="flex"/>
        <p class="relative text-sm font-medium leading-6 text-gray-900 dark:text-gray-400">Sign in with Web3:</p>

        <div class="mt-2 grid grid-rows-3 gap-3">
            <div>
                <a class="inline-flex w-full justify-center rounded-md bg-white px-3 py-2 text-gray-500 shadow-sm ring-1 ring-inset ring-gray-300 hover:bg-gray-50 focus:outline-offset-0"
                    href="#" @click="comingSoon"> <img alt="Sign in with WalletConnect" aria-hidden="true" class="h-5 w-full"
                        fill="currentColor" src="/svg/walletconnect-text.svg" /> </a>
            </div>

            <div>
                <a class="inline-flex w-full justify-center rounded-md bg-white px-3 py-2 text-gray-500 shadow-sm ring-1 ring-inset ring-gray-300 hover:bg-gray-50 focus:outline-offset-0"
                    href="#" @click="comingSoon"> <img aria-hidden="true" class="h-5 w-5" fill="currentColor" src="/svg/tezos.svg" /> <span
                        class="ml-1 text-gray-800 font-semibold h-5">Tezos</span> </a>
            </div>

            <div>
                <a class="inline-flex w-full justify-center rounded-md bg-white px-3 py-2 text-gray-500 shadow-sm ring-1 ring-inset ring-gray-300 hover:bg-gray-50 focus:outline-offset-0"
                    href="#" @click="comingSoon"> <img aria-hidden="true" class="h-5 w-5" fill="currentColor" src="/svg/near.svg" /> <span
                        class="ml-1 text-gray-800 font-semibold h-5">NEAR</span> </a>
            </div>
          <div>
            <a class="inline-flex w-full justify-center rounded-md bg-white px-3 py-2 text-gray-500 shadow-sm ring-1 ring-inset ring-gray-300 hover:bg-gray-50 focus:outline-offset-0"
               href="#" @click="connectToMyAlgo"> <img aria-hidden="true" class="h-5 w-5" fill="currentColor" src="/svg/algorand-algo-logo.png" /> <span
                class="ml-1 text-gray-800 font-semibold h-5">Algorand</span> </a>
          </div>
        </div>
    </div>
</template>
<script setup lang="ts">

import CloseButton from "./CloseButton.vue";
import ActionResultModal from "~/components/modals/ActionResultModal.vue";
import useModalStore from "~/stores/useModalStore";
import MyAlgoConnect  from "@randlabs/myalgo-connect";
import {useUserStore} from "~/stores/user";
import { storeToRefs } from 'pinia';


const store = useModalStore();
const isLogin = ref(false)
const error = ref({})
const success = ref(false)

const userStore = useUserStore()
const { user } = storeToRefs(userStore)
const {status, data, signIn} = useAuth()
const myAlgoWallet = new MyAlgoConnect();

const settings = {
  shouldSelectOneAccount: true,
  // openManager: true
};
async function connectToMyAlgo() {
  console.log("logging in")
//   try {
  const accounts = await myAlgoWallet.connect(settings);
  const account = accounts[0]

  const userData = {
    address: account.address,
    username: account.name,
    ecosystem: "algorand",
  }
  await signIn({address: userData.address, ecosystem: userData.ecosystem, type: "address"}, { callbackUrl: '/settings/tokens' })
      .then(data => {
        store.closeModal()
        user.value = {
          id: userData.address ,
          username: userData.username,
        }
      })
  isLogin.value = true
  //   } catch (err) {
//     console.error(err);
//   }
}

function comingSoon(){
    store.openModal({
            component: ActionResultModal,
            props: {
                title: "Coming soon",
                message: "Stay tuned for updates.",
                isError: true
            },
        });
}
</script>
