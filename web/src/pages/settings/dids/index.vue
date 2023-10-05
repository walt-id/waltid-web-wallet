<template>
  <CenterMain>
    <div class="flex items-center justify-between border-b mb-5">
        <h1 class="py-3 text-2xl font-normal">DIDs</h1>
      <div class="flex">
        <button
          class="inline-flex items-center rounded-lg bg-blue-500 px-9 py-2 text-sm font-thin text-white shadow-sm hover:bg-blue-600 focus-visible:outline focus-visible:outline-2 focus-visible:outline-offset-2 focus-visible:outline-blue-600"
          @click="createDid"
        >
          <!-- <KeyIcon aria-hidden="true" class="mr-1 h-5 w-5 text-white" /> -->
          <span>New</span>
        </button>
      </div>
    </div>

    <ol class="list-decimal" role="list">
      <li
        v-for="did in dids.value"
        :key="did"
        class="flex relative  items-center justify-between gap-x-6 border-b pt-3 h-20 bg-white hover:bg-gray-50 hover:rounded-lg"
      >
        <div class=" flex flex-none absolute inset-x-0 bottom-0 items-center gap-x-4 w-full ">
          <NuxtLink
            :to="'/settings/dids/' + did.did"
            class="hidden rounded-md  px-2.5 py-1 text-sm ring-inset w-full ring-gray-300  sm:block"
          >
            <div class="min-w-0 max-w-4xl overflow-scroll overflow-hidden	">
              <div class="flex items-start gap-x-3 " >
                <p class="mx-2 text-base font-medium leading-6 text-gray-900 ">
                  {{ did.alias }}
                </p>
              </div>
              <div class="flex items-start gap-x-3">
                <p class="mx-2 text-base font-normal leading-6 text-gray-500">
                  {{ did.did }}
                </p>
              </div>
            </div>
          </NuxtLink>
          <div v-if="!did.default" class="rounded-full bg-cyan-100 gap-x-3 font-medium text-xs text-cyan-900 mr-2 ">
            <span class="p-5 ">
              Default
            </span>
          </div>
        </div>
      </li>
    </ol>
    <p v-if="dids && dids.length == 0" class="mt-2">No DIDs.</p>
  </CenterMain>
</template>

<script setup>
import CenterMain from "~/components/CenterMain.vue";
import { KeyIcon, InboxArrowDownIcon } from "@heroicons/vue/24/outline";

const dids = ref("");

async function loadDids() {
  dids.value = await useLazyFetch("/r/wallet/dids").data;
  refreshNuxtData();
}
loadDids();
console.log(dids);

function createDid() {
  navigateTo("dids/create");
}

useHead({
  title: "DIDs - walt.id",
});
</script>

<style scoped></style>
