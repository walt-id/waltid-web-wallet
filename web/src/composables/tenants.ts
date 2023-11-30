export function useTenant() {
    return useState("tenant-config", async () => {
        const baseTenantDomain = useRequestURL().host.replace(":3000", ":8080");
        console.log("Base tenant domain is: ", baseTenantDomain)

        const { data , error } = await useFetch("http://" + baseTenantDomain + "/config/wallet");
        console.log("Tenant configuration: ", data.value)

        if (error.value) {
            console.log("Tenant error: ", error.value.message)
            throw createError({
                statusCode: 404,
                statusMessage: "Tenant error: " + error.value?.data?.message,
                fatal: true
            })
        }

        return data.value;
    });
}
