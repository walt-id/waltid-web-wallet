<template>
  <div>
    <PageHeader>
      <template v-slot:title>
        <div class="ml-3">
          <h1
            class="text-2xl font-bold leading-7 text-gray-900 sm:truncate sm:leading-9"
          >
            Receive {{ credentialCount === 1 ? "single" : credentialCount }}
            {{ credentialCount === 1 ? "credential" : "credentials" }}
          </h1>
          <p>
            issued by <span class="underline">{{ issuerHost }}</span>
          </p>
        </div>
      </template>

      <template v-if="!immediateAccept" v-slot:menu>
        <ActionButton
          class="inline-flex items-center rounded-md bg-red-600 px-3 py-2 text-sm font-semibold text-white shadow-sm hover:scale-105 hover:animate-pulse hover:bg-red-700 focus:animate-none focus:outline focus:outline-offset-2 focus:outline-red-700"
          display-text="Reject"
          icon="heroicons:x-mark"
          type="button"
          @click="navigateTo('/')"
        />

        <ActionButton
          :class="[
            failed
              ? 'animate-pulse bg-red-600 hover:scale-105 hover:bg-red-700 focus:outline focus:outline-offset-2 focus:outline-red-700'
              : 'bg-green-600 hover:scale-105 hover:animate-pulse hover:bg-green-700 focus:animate-none focus:outline-green-700',
          ]"
          :failed="failed"
          :disabled = "!selected.startsWith('did')"
          class="inline-flex items-center rounded-md px-3 py-2 text-sm font-semibold text-white shadow-sm focus:outline focus:outline-offset-2"
          display-text="Accept"
          icon="heroicons:check"
          type="button"
          @click="acceptCredential"
        />
      </template>
    </PageHeader>

    <CenterMain>
      <h1 class="mb-2 text-2xl font-semibold">Issuance</h1>

      <LoadingIndicator v-if="immediateAccept" class="my-6 mb-12 w-full">
        Receiving {{ credentialCount }} credential(s)...
      </LoadingIndicator>

      <Listbox as="div" v-model="selected">
        <ListboxLabel class="block top-0 z-10 border-y border-b-gray-200 border-t-gray-100 bg-gray-50 px-3 py-1.5 text-sm font-semibold leading-6 text-gray-900">
            <h3>Subject:</h3>
         </ListboxLabel>
        <div class="relative mt-2">
          <ListboxButton class=" overflow-y-auto shadow-xl relative w-full cursor-default bg-white py-1.5 pl-3 pr-10 text-left text-gray-900 ring-gray-300 focus:outline-none focus:ring-2 focus:ring-blue-600 sm:text-sm sm:leading-6">
            <span class="block truncate">{{ selected }}</span>
            <span
              class="pointer-events-none absolute inset-y-0 right-0 flex items-center pr-2"
            >
              <ChevronUpDownIcon
                class="h-5 w-5 text-gray-400"
                aria-hidden="true"
              />
            </span>
          </ListboxButton>

          <transition
            leave-active-class="transition ease-in duration-100"
            leave-from-class="opacity-100"
            leave-to-class="opacity-0"
          >
            <ListboxOptions
              class="absolute z-10 mt-1 max-h-60 w-full overflow-auto rounded-md bg-white py-1 text-base shadow-lg ring-1 ring-black ring-opacity-5 focus:outline-none sm:text-sm"
            >
              <ListboxOption
                as="template"
                v-for="did in dids.value"
                :value="did"
                v-slot="{ active, selected }"
              >
                <li
                  :class="[
                    active ? 'bg-blue-600 text-white' : 'text-gray-900',
                    'relative cursor-default select-none py-2 pl-8 pr-4',
                  ]"
                >
                  <span
                    :class="[
                      selected ? 'font-semibold' : 'font-normal',
                      'block truncate',
                    ]"
                    >{{ did }}</span
                  >

                  <span
                    v-if="selected"
                    :class="[
                      active ? 'text-white' : 'text-indigo-600',
                      'absolute inset-y-0 left-0 flex items-center pl-1.5',
                    ]"
                  >
                    <CheckIcon class="h-5 w-5" aria-hidden="true" />
                  </span>
                </li>
              </ListboxOption>
            </ListboxOptions>
          </transition>
        </div>
      </Listbox>
      <br>
      <p class="mb-1">The following credentials will be issued:</p>

      <div
        aria-label="Credential list"
        class="h-full overflow-y-auto shadow-xl"
      >
        <div
          v-for="group in groupedCredentialTypes.keys()"
          :key="group.id"
          class="relative"
        >
          <div
            class="top-0 z-10 border-y border-b-gray-200 border-t-gray-100 bg-gray-50 px-3 py-1.5 text-sm font-semibold leading-6 text-gray-900"
          >
            <h3>{{ group }}s:</h3>
          </div>
          <ul class="divide-y divide-gray-100" role="list">
            <li
              v-for="credential in groupedCredentialTypes.get(group)"
              :key="credential"
              class="flex gap-x-4 px-3 py-5"
            >
              <CredentialIcon
                :credentialType="credential.name"
                class="h-6 w-6 flex-none rounded-full bg-gray-50"
              ></CredentialIcon>

              <div class="flex min-w-0 flex-row items-center">
                <span class="text-lg font-semibold leading-6 text-gray-900"
                  >{{ credential.id }}.</span
                >
                <span class="ml-1 truncate text-sm leading-5 text-gray-800">{{
                  credential.name
                }}</span>
              </div>
            </li>
          </ul>
        </div>
      </div>
      <br />
      <p class="mb-1">Subject:</p>
      <!-- <div class="h-full overflow-y-auto shadow-xl">
        <select v-model="selected">
          <option v-for="did in dids.value" :value="did">
            {{ did }}
          </option>
        </select>
      </div> -->
    </CenterMain>
  </div>
</template>

<script lang="ts" setup>
import CenterMain from "~/components/CenterMain.vue";
import PageHeader from "~/components/PageHeader.vue";
import CredentialIcon from "~/components/CredentialIcon.vue";
import ActionButton from "~/components/buttons/ActionButton.vue";
import LoadingIndicator from "~/components/loading/LoadingIndicator.vue";
import {
  Listbox,
  ListboxButton,
  ListboxLabel,
  ListboxOption,
  ListboxOptions,
} from "@headlessui/vue";
import { CheckIcon, ChevronUpDownIcon } from "@heroicons/vue/20/solid";
import { useTitle } from "@vueuse/core";

// const selected = ref('')
const dids = ref();

async function loadDids() {
  dids.value = await useLazyFetch("/r/wallet/dids").data;
  refreshNuxtData();
  
}
loadDids();
const selected = ref('Please select a did')

const query = useRoute().query;

const request = decodeRequest(query.request);
console.log("Issuance -> Using request: ", request);

const immediateAccept = ref(false);
console.log("Making issuanceUrl...");
const issuanceUrl = new URL(request);
console.log("issuanceUrl: ", issuanceUrl);

const credentialOffer = issuanceUrl.searchParams.get("credential_offer");
console.log("credentialOffer: ", credentialOffer);

if (credentialOffer == null) {
  throw createError({
    statusCode: 400,
    statusMessage: "Invalid issuance request: No credential_offer",
  });
}

const issuanceParamsJson = JSON.parse(credentialOffer);
console.log("issuanceParamsJson: ", issuanceParamsJson);

console.log("Issuer host...");
const issuer = issuanceParamsJson["credential_issuer"];

let issuerHost: String;
try {
  issuerHost = new URL(issuer).host;
} catch {
  issuerHost = issuer;
}

console.log("Issuer host:", issuerHost);
const credentialList = issuanceParamsJson["credentials"];

let credentialTypes: String[] = [];

for (let credentialListElement of credentialList) {
  const typeList = credentialListElement["types"] as Array<String>;
  const lastType = typeList[typeList.length - 1] as String;

  credentialTypes.push(lastType);
}

const credentialCount = credentialTypes.length;

let i = 0;
const groupedCredentialTypes = groupBy(
  credentialTypes.map((item) => {
    return { id: ++i, name: item };
  }),
  (c) => c.name
);

const failed = ref(false);

async function acceptCredential() {
  console.log(selected.value);
  try {
    await $fetch(`/r/wallet/exchange/useOfferRequest?did=${selected.value}`, {
      method: "POST",
      body: request,
    });
    navigateTo("/");
  } catch (e) {
    failed.value = true;
    throw e;
  }
}

if (query.accept) {
  // TODO make accept a JWT or something wallet-backend secured
  immediateAccept.value = true;
  acceptCredential();
}

/*if (query.request) {
    const request = atob(query.request)
    console.log(request)
} else {
    console.error("No request")
}*/

useTitle(`Claim credentials - walt.id`);
</script>

<style scoped></style>
