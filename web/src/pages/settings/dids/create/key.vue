<template>
    <DidCreationView :didParams="{useJwkJcsPub: useJwkJcsPub}" method="key">
        
        <div class="sm:grid sm:grid-cols-3 sm:items-start sm:gap-4 sm:py-4">
            <label class="block text-sm font-medium leading-6 text-gray-900 sm:pt-1.5" for="format">Use jwk_jcs-pub format</label>
            <div class="mt-2 sm:col-span-2 sm:mt-0">
                <input id="default-checkbox" 
                type="checkbox"
                class="w-4 h-4 text-blue-600 bg-gray-100 border-gray-300 rounded focus:ring-blue-500 dark:focus:ring-blue-600 dark:ring-offset-gray-800 focus:ring-2 dark:bg-gray-700 dark:border-gray-600"
                v-model="useJwkJcsPub"/>
            </div>
        </div>

    </DidCreationView>
</template>

<script lang="ts" setup>
import DidCreationView from "~/components/dids/DidCreationView.vue";

const useJwkJcsPub = ref(false)

</script>
