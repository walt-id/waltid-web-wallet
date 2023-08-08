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
                    href="#" @click="loginWithNear"> <img aria-hidden="true" class="h-5 w-5" fill="currentColor" src="/svg/near.svg" /> <span
                        class="ml-1 text-gray-800 font-semibold h-5">NEAR</span> </a>
            </div>
          <div>
            <a class="inline-flex w-full justify-center rounded-md bg-white px-3 py-2 text-gray-500 shadow-sm ring-1 ring-inset ring-gray-300 hover:bg-gray-50 focus:outline-offset-0"
               href="#" @click="connectToMyAlgo"> <img aria-hidden="true" class="h-5 w-5" fill="currentColor" src="/svg/algorand-algo-logo.svg" /> <span
                class="ml-1 tuseUserStoreext-gray-800 font-semibold h-5">Algorand</span> </a>
          </div>
        </div>
    </div>
</template>
<script setup lang="ts">

import CloseButton from "./CloseButton.vue";
import ActionResultModal from "~/components/modals/ActionResultModal.vue";
import useModalStore from "~/stores/useModalStore";
import MyAlgoConnect  from "@randlabs/myalgo-connect";
import { useUserStore } from "~/stores/user";
import { storeToRefs } from 'pinia';


import {setupNearWallet} from "@near-wallet-selector/near-wallet";
import {setupMyNearWallet} from "@near-wallet-selector/my-near-wallet";
import {setupSender} from "@near-wallet-selector/sender";
import {setupHereWallet} from "@near-wallet-selector/here-wallet";
import {setupMathWallet} from "@near-wallet-selector/math-wallet";
import {setupNightly} from "@near-wallet-selector/nightly";
import {setupMeteorWallet} from "@near-wallet-selector/meteor-wallet";
import {setupNarwallets} from "@near-wallet-selector/narwallets";
import {setupWelldoneWallet} from "@near-wallet-selector/welldone-wallet";
//TODO: generates "ReferenceError: Buffer is not defined" at runtime
// import {setupLedger} from "@near-wallet-selector/ledger";
import {setupNearFi} from "@near-wallet-selector/nearfi";
import {setupCoin98Wallet} from "@near-wallet-selector/coin98-wallet";
import {setupOptoWallet} from "@near-wallet-selector/opto-wallet";
import {setupNeth} from "@near-wallet-selector/neth";
import {setupModal} from "@near-wallet-selector/modal-ui";
import {setupWalletSelector} from "@near-wallet-selector/core";

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


async function loginWithNear() {
  store.closeModal();
  const selector = await setupWalletSelector({
    network: "testnet",
    modules: [
      setupNearWallet(),
      setupMyNearWallet(),
      setupSender(),
      setupHereWallet(),
      setupMathWallet(),
      setupNightly(),
      setupMeteorWallet(),
      setupNarwallets(),
      setupWelldoneWallet(),
      // setupLedger(),
      setupNearFi(),
      setupCoin98Wallet(),
      setupOptoWallet(),
      setupNeth(),
    ],
  });

  const modal = setupModal(selector, {
    contractId: "", methodNames: undefined, theme: undefined,
    description: "connect to a wallet"
  });
  modal.show();




    modal.on("onHide" , async (event) => {

      console.log("event", event)
      if (event.hideReason == "wallet-navigation") {

          const isSignedIn = selector.isSignedIn();

          if ( isSignedIn) {
            const selectedAccount = selector.store.getState().accounts[0].accountId;


            const userData = {
              address: selectedAccount,
              username: selectedAccount,
              ecosystem: "near",
            };

            user.value = {
              id: userData.address ,
              username: userData.username,
            }

            try {
              await signIn(
                  {
                    address: userData.address,
                    ecosystem: userData.ecosystem,
                    type: "address",
                  },
                  { callbackUrl: '/settings/tokens' }
              );
              store.closeModal();

              isLogin.value = true;
            } catch (error) {
              // Handle any errors that might occur during the signIn process
              console.error("Error during signIn:", error);
            }
          } else {
            console.log("No account selected");
          }
      }
  });
}

</script>
