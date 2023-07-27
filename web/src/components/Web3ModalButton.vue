<template>
    <div>
        <div class="w-full inline-flex justify-center">
            <button @click="openWeb3()" class="relative inline-flex items-center justify-center p-4 px-6 py-3 overflow-hidden font-medium text-indigo-600 transition duration-300 ease-out border-2 border-purple-500 rounded-full shadow-md group">
                <span class="absolute inset-0 flex items-center justify-center w-full h-full text-white duration-300 -translate-x-full bg-purple-500 group-hover:translate-x-0 ease">
                <svg class="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24" xmlns="http://www.w3.org/2000/svg"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M14 5l7 7m0 0l-7 7m7-7H3"></path></svg>
                </span>
                <span class="absolute flex items-center justify-center w-full h-full text-purple-500 transition-all duration-300 transform group-hover:translate-x-full ease">Connect with web3</span>
                <span class="relative invisible">Connect with web3</span>
            </button>
        </div>
        <!-- <w3m-core-button></w3m-core-button> -->
    </div>
</template>
<script setup>
import { EthereumClient, w3mConnectors, w3mProvider } from '@web3modal/ethereum'
import { Web3Modal } from '@web3modal/html'
import { configureChains, createConfig } from '@wagmi/core'
import { arbitrum, avalanche, mainnet, polygon } from "@wagmi/core/chains";
const config = useRuntimeConfig()

// 1. Define constants
const projectId = "7b5d7bd97ff42210ca029d691bfabfc4"
// const projectId = config.public.projectId;
if (!projectId) {
    throw new Error("You need to provide PROJECT_ID env variable");
}

const chains = [mainnet, polygon, avalanche, arbitrum];

// 2. Configure wagmi client
const { publicClient } = configureChains(chains, [w3mProvider({ projectId })])
const wagmiConfig = createConfig({
    autoConnect: true,
    connectors: w3mConnectors({ projectId, chains }),
    publicClient
})
// 3. Create ethereum and modal clients
const ethereumClient = new EthereumClient(wagmiConfig, chains)
const web3Modal = new Web3Modal({ projectId }, ethereumClient)
const subscribe = web3Modal.subscribeModal(newState => console.log(newState))

function openWeb3() {
    web3Modal.openModal().then(() => {
        subscribe()
    }).catch(err => {
        console.error(err)
    })
}
</script>
