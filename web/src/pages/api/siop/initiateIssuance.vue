<template>
    <CenterMain>
        <LoadingIndicator>Loading issuance parameters...</LoadingIndicator>
    </CenterMain>
</template>

<script lang="ts" setup>
import CenterMain from "~/components/CenterMain.vue";
import LoadingIndicator from "~/components/loading/LoadingIndicator.vue";

if (process.client) {
    const url = "openid-initiate-issuance://" + (new URL(window.location.href).search)
    console.log(url);
    const encoded = btoa(url)
    console.log(encoded)

    navigateTo(`/exchange/issuance?request=${encoded}`)
}
</script>

<style scoped>

</style>
