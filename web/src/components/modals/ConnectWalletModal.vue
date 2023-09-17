<template>
  <div>
    <CloseButton class="flex" />
    <p
      class="relative text-sm font-medium leading-6 text-gray-900 dark:text-gray-400"
    >
      Sign in with Web3:
    </p>

    <div class="mt-2 grid grid-rows-3 gap-3">
      <!-- <div>
        <a
          class="inline-flex w-full justify-center rounded-md bg-white px-3 py-2 text-gray-500 shadow-sm ring-1 ring-inset ring-gray-300 hover:bg-gray-50 focus:outline-offset-0"
          href="#"
          @click="beaconTezosWallet"
        >
          <img
            aria-hidden="true"
            class="h-5 w-5"
            fill="currentColor"
            src="/svg/tezos.svg"
          />
          <span class="ml-1 h-5 font-semibold text-gray-800">Tezos</span>
        </a>
      </div> -->
      <!-- <div>
        <a
          class="inline-flex w-full justify-center rounded-md bg-white px-3 py-2 text-gray-500 shadow-sm ring-1 ring-inset ring-gray-300 hover:bg-gray-50 focus:outline-offset-0"
          href="#"
          @click="polkadotjsWallet"
        >
           <img
            aria-hidden="true"
            class="h-5 w-5"
            fill="currentColor"
            src="/svg/tezos.svg"
          /> 
          <span class="ml-1 h-5 font-semibold text-gray-800">Polkadot</span>
        </a>
      </div> -->
      <div class="flex gap-1">
        <w3m-core-button label="Login with WalletConnect" />
      </div>
      <div>
        <a
          class="inline-flex w-full justify-center rounded-md bg-white px-3 py-2 text-gray-500 shadow-sm ring-1 ring-inset ring-gray-300 hover:bg-gray-50 focus:outline-offset-0"
          href="#"
          @click="loginWithNear"
        >
          <img
            aria-hidden="true"
            class="h-5 w-5"
            fill="currentColor"
            src="/svg/near.svg"
          />
          <span class="ml-1 h-5 font-semibold text-gray-800">NEAR</span>
        </a>
      </div>
      <div>
        <a
          class="inline-flex w-full justify-center rounded-md bg-white px-3 py-2 text-gray-500 shadow-sm ring-1 ring-inset ring-gray-300 hover:bg-gray-50 focus:outline-offset-0"
          href="#"
          @click="connectToMyAlgo"
        >
          <img
            aria-hidden="true"
            class="h-5 w-5"
            fill="currentColor"
            src="/svg/algorand-algo-logo.svg"
          />
          <span class="ml-1 h-5 font-semibold text-gray-800">Algorand</span>
        </a>
      </div>
    </div>
  </div>
</template>
<script setup lang="ts">
import CloseButton from "./CloseButton.vue";
import ActionResultModal from "~/components/modals/ActionResultModal.vue";
import useModalStore from "~/stores/useModalStore";
import MyAlgoConnect from "@randlabs/myalgo-connect";
import { useUserStore } from "~/stores/user";
import { storeToRefs } from "pinia";

import { setupNearWallet } from "@near-wallet-selector/near-wallet";
import { setupMyNearWallet } from "@near-wallet-selector/my-near-wallet";
import { setupSender } from "@near-wallet-selector/sender";
import { setupHereWallet } from "@near-wallet-selector/here-wallet";
import { setupMathWallet } from "@near-wallet-selector/math-wallet";
import { setupNightly } from "@near-wallet-selector/nightly";
import { setupNarwallets } from "@near-wallet-selector/narwallets";
import { setupWelldoneWallet } from "@near-wallet-selector/welldone-wallet";
//TODO: generates "ReferenceError: Buffer is not defined" at runtime
// import {setupLedger} from "@near-wallet-selector/ledger";
import { setupNearFi } from "@near-wallet-selector/nearfi";
import { setupCoin98Wallet } from "@near-wallet-selector/coin98-wallet";
import { setupOptoWallet } from "@near-wallet-selector/opto-wallet";
import { setupModal } from "@near-wallet-selector/modal-ui";
import { setupWalletSelector } from "@near-wallet-selector/core";

//import { BeaconWallet } from "@taquito/beacon-wallet";
//import { TezosToolkit } from "@taquito/taquito";
import {
  EthereumClient,
  w3mConnectors,
  w3mProvider,
} from "@web3modal/ethereum";
import { Web3Modal } from "@web3modal/html";
import { configureChains, createConfig } from "@wagmi/core";
import { mainnet } from "@wagmi/core/chains";
import { defineChain } from "viem";
import { getAccount } from "@wagmi/core";
import { ModalCtrlState } from "@web3modal/core/dist/_types/src/types/controllerTypes";

import { web3Enable, web3Accounts } from "@polkadot/extension-dapp";

const store = useModalStore();
const isLogin = ref(false);
const error = ref({});
const success = ref(false);

const userStore = useUserStore();
const { user } = storeToRefs(userStore);
const { status, data, signIn } = useAuth();

const projectId = "92fa9ca65f8b1bb7b140bee3dcd3245e";

const shimmer = defineChain({
  id: 1072,
  name: "ShimmerEVM Testnet",
  network: "shimmer",
  nativeCurrency: {
    decimals: 18,
    name: "SMR",
    symbol: "SMR",
  },
  rpcUrls: {
    default: {
      http: ["https://json-rpc.evm.testnet.shimmer.network"],
      //webSocket: ['wss://rpc.zora.energy'],
    },
    public: {
      http: ["https://json-rpc.evm.testnet.shimmer.network"],
      //webSocket: ['wss://rpc.zora.energy'],
    },
  },
  blockExplorers: {
    default: {
      name: "Explorer",
      url: "https://explorer.evm.testnet.shimmer.network",
    },
  },
});

const chains = [mainnet, shimmer];

const { publicClient } = configureChains(chains, [w3mProvider({ projectId })]);

const wagmiConfig = createConfig({
  autoConnect: true,
  connectors: w3mConnectors({ projectId, chains }),
  publicClient,
});

const ethereumClient = new EthereumClient(wagmiConfig, chains);

async function onModalUpdate(newState: ModalCtrlState) {
  console.log("New modal state: ", newState);
  store.closeModal();

  if (!newState.open) {
    const account = getAccount();
    console.log("Wagmi account: ", account);

    if (account.status === "connected" && account.address.startsWith("0x")) {
      console.log("WE WILL NOW CONTINUE");

      const userData = {
        address: account.address,
        username: account.address,
        ecosystem: "ethereum",
      };
      await signIn(
        {
          address: userData.address,
          ecosystem: "ethereum",
          type: "address",
        },
        { callbackUrl: "/settings/tokens" }
      ).then((data) => {
        user.value = {
          id: userData.address,
          username: userData.username,
        };
      });
      isLogin.value = true;
    }
  }
}

if (process.client) {
  const web3modal = new Web3Modal(
    {
      projectId,
      themeVariables: {
        "--w3m-accent-color": "#FFF",
        "--w3m-accent-fill-color":"#000"
      },
    },
    ethereumClient
  );

  web3modal.subscribeModal(async (newState) => {
    await onModalUpdate(newState);
  });
}



const config = useRuntimeConfig();

const myAlgoWallet = new MyAlgoConnect();
const settings = {
  shouldSelectOneAccount: true,
};
async function connectToMyAlgo() {
  console.log("logging in");
  //   try {
  const accounts = await myAlgoWallet.connect(settings);
  const account = accounts[0];

  const userData = {
    address: account.address,
    username: account.name,
    ecosystem: "algorand",
  };
  await signIn(
    {
      address: userData.address,
      ecosystem: userData.ecosystem,
      type: "address",
    },
    { callbackUrl: "/settings/tokens" }
  ).then((data) => {
    store.closeModal();
    user.value = {
      id: userData.address,
      username: userData.username,
    };
  });
  isLogin.value = true;
}
function comingSoon() {
  store.openModal({
    component: ActionResultModal,
    props: {
      title: "Coming soon",
      message: "Stay tuned for updates.",
      isError: true,
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
      setupNarwallets(),
      // setupNeth(),
      setupWelldoneWallet(),
      // setupLedger(),
      setupNearFi(),
      setupCoin98Wallet(),
      setupOptoWallet(),
    ],
  });

  const modal = setupModal(selector, {
    contractId: "",
    methodNames: undefined,
    theme: undefined,
    description: "connect to a wallet",
  });
  modal.show();

  modal.on("onHide", async (event) => {
    console.log("event", event);
    if (event.hideReason == "wallet-navigation") {
      const isSignedIn = selector.isSignedIn();

      if (isSignedIn) {
        const selectedAccount = selector.store.getState().accounts[0].accountId;

        const userData = {
          address: selectedAccount,
          username: selectedAccount,
          ecosystem: "near",
        };

        user.value = {
          id: userData.address,
          username: userData.username,
        };

        try {
          await signIn(
            {
              address: userData.address,
              ecosystem: userData.ecosystem,
              type: "address",
            },
            { callbackUrl: "/settings/tokens" }
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
async function polkadotjsWallet() {
  // Request permission to access accounts
  const extensions = await web3Enable("Walt.id | Wallet");
  if (extensions.length === 0) {
    // No extension installed, or the user did not accept the authorization
    return;
  }

  // Get all the accounts
  const allAccounts = await web3Accounts();
  if (allAccounts.length === 0) {
    // No account has been found
    return;
  }

  // Use the first account
  const account = allAccounts[0];

  const userData = {
    address: account.address,
    username: account.address,
    ecosystem: "algorand",
  };

  await signIn(
    {
      address: userData.address,
      ecosystem: userData.ecosystem,
      type: "address",
    },
    { callbackUrl: "/settings/tokens" }
  ).then((data) => {
    store.closeModal();
    user.value = {
      id: userData.address,
      username: userData.username,
    };
  });
  isLogin.value = true;
}
</script>
