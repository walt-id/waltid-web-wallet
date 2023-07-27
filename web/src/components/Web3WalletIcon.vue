<template>
    <!-- <div class="inline-flex justify-center px-3 py-2"> -->
    <div>
        <img src="/svg/ethereum.svg" v-if="ecosystem === 'ethereum'" alt="ethereum" aria-hidden="true" class="h-5 w-full" fill="currentColor"/>
        <img src="/svg/tezos.svg" v-if="ecosystem === 'tezos'" alt="tezoz" aria-hidden="true" class="h-5 w-full" fill="currentColor"/>
        <img src="/svg/near.svg" v-if="ecosystem === 'near'" alt="near" aria-hidden="true" class="h-5 w-full" fill="currentColor"/>
        <img src="/svg/flow.svg" v-if="ecosystem === 'flow'" alt="flow" aria-hidden="true" class="h-5 w-full" fill="currentColor"/>
    </div>
</template>

<script setup>
const props = defineProps(["ecosystem"])
</script>

<style scoped>

</style>
