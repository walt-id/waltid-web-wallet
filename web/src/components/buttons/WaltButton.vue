<template>
    <button class="mb-2 border rounded-xl p-2 bg-blue-500 text-white flex flex-row justify-center items-center">
        <component :is="icon" class="w-5 h-5 mr-1" aria-hidden="true"/><!--v-if="props.icon"-->
        <span><slot/></span>
    </button>
</template>

<script setup>
const props = defineProps(["icon"])

const icon = ref(props.icon)
</script>

<style scoped>

</style>
