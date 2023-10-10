<template>
  <CenterMain>
    <div class="mb-5 flex items-center justify-between border-b">
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
        class="relative flex h-20  items-center justify-between gap-x-6 border-b bg-white pt-3 hover:rounded-lg hover:bg-gray-50"
      >
        <div
          class=" inset-x-0 bottom-0 flex w-full h-full min-h-content flex-none items-end gap-x-4"
        >
          <NuxtLink
            :to="'/settings/dids/' + did.did"
            class="grid h-full w-full grid-cols-8 items-end gap-2 rounded-md px-2.5 py-1"
          >
            <div class="col-span-6 min-w-0">
              <div class="flex items-start gap-x-3">
                <p class="mx-2 text-base font-medium leading-6 text-gray-900">
                  {{ did.alias }}
                </p>
              </div>
              <div class="flex items-start gap-x-3">
                <p
                  class="mx-2 overflow-x-auto text-base font-normal leading-6 text-gray-500"
                >
                  {{ did.did }}
                </p>
              </div>
            </div>
            <div v-if="did.default" class="col-span-2 mb-1 justify-self-end">
              <span class="mr-1 gap-x-3 rounded-full bg-cyan-100 px-3 py-0.5 text-xs font-medium text-cyan-900" >
                Default
              </span>
            </div>
          </NuxtLink>
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
