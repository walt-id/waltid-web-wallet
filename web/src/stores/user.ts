import { defineStore } from "pinia"

export const useUserStore = defineStore('userStore', () => {
  const user = ref(useLocalStorage('id/walt/wallet/user', { id: "", username: "n/a" }))

  return { user }
})