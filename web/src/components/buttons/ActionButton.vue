<template>
    <button
            :disabled="loading"
            class=""
            @click="handleClick">

        <component :is="icon" v-if="!loading" class="h-5 w-5 mr-1"/>
        <InlineLoadingCircle v-else-if="loading" class="h-5 w-5 pr-2"/>
        <span v-if="!loading">{{ props.displayText }}</span>
        <span v-else>{{ props.actionText ?? props.displayText }}</span>
    </button>
</template>

<script setup>
import InlineLoadingCircle from "~/components/loading/InlineLoadingCircle.vue";

const emit = defineEmits(["click"])
const props = defineProps(["handler", "icon", "displayText", "actionText"])

const icon = ref(props.icon)

const loading = ref(false)

function handleClick() {
    loading.value = true
    emit('click')
}
</script>

<style scoped>

</style>
