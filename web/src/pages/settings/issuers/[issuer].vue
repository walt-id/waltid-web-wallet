<template>
    <CenterMain>
      <BackButton />
      <ol class="divide-y divide-gray-100 list-decimal border rounded-2xl mt-2 px-2" role="list">
            <li v-for="credential in credentials" :key="credential" class="flex items-center justify-between gap-x-6 py-4">
                <div class="min-w-0">
                    <div class="flex items-start gap-x-3">
                        <p class="mx-2 text-base font-semibold leading-6 text-gray-900">{{ credential.type }}</p>
                    </div>
                </div>
                <div class="flex flex-none items-center gap-x-4">
                    <NuxtLink @click="requestCredential(credential)"
                              class="hidden rounded-md bg-white px-2.5 py-1.5 text-sm font-semibold text-gray-900 shadow-sm ring-1 ring-inset ring-gray-300 hover:bg-gray-50 sm:block"
                              type="button">
                        Request credential offer
                    </NuxtLink>
                </div>
            </li>
        </ol>
        <p v-if="issuers && issuers.length == 0" class="mt-2">No issuers.</p>
    </CenterMain>
  </template>
  
  <script lang="ts" setup>
  import CenterMain from "~/components/CenterMain.vue";
  import BackButton from "~/components/buttons/BackButton.vue";
  
  const route = useRoute();
  
  const issuer = route.params.issuer;
  
  const credentials = await $fetch(`/r/wallet/issuers/${issuer}/credentials`);
  refreshNuxtData();
  
  function requestCredential(credential: any){
    console.log(credential)
  }
  
  useHead({
    title: `${issuer} Supported credentials`,
  });
  </script>
  
  <style scoped></style>
  