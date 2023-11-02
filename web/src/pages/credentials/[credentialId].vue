<template>
    <CenterMain>
        <BackButton/>
        <LoadingIndicator v-if="pending">Loading credential...</LoadingIndicator>
        <div v-else>
            <div class="flex justify-center items-center my-10">
                <div class="bg-white p-6 rounded-2xl shadow-2xl h-full w-[350px]">
                    <div class="flex justify-end">
                        <div
                                :class="jwtJson.vc.expirationDate ? new Date(jwtJson.vc.expirationDate).getTime() > new Date().getTime() ? 'bg-cyan-50' : 'bg-red-50' : 'bg-cyan-50'"
                                class="rounded-lg px-3 mb-2"
                        >
                            <div
                                    :class="jwtJson.vc.expirationDate ? new Date(jwtJson.vc.expirationDate).getTime() > new Date().getTime() ? 'text-cyan-900' : 'text-orange-900' : 'text-cyan-900'"
                            >
                                {{
                                    jwtJson.vc.expirationDate
                                            ? new Date(jwtJson.vc.expirationDate).getTime() >
                                            new Date().getTime()
                                                    ? "Valid"
                                                    : "Expired"
                                            : "Valid"
                                }}
                            </div>
                        </div>
                    </div>
                    <h2 class="text-2xl font-bold mb-2 text-gray-900 bold mb-8">
                        {{
                            jwtJson.vc.type[jwtJson.vc.type.length - 1].replace(
                                    /([a-z0-9])([A-Z])/g,
                                    "$1 $2"
                            )
                        }}
                    </h2>
                    <div v-if="jwtJson.vc.issuer" class="flex items-center">
                        <img :src="jwtJson.vc.issuer?.image?.id ? jwtJson.vc.issuer?.image?.id : jwtJson.vc.issuer?.image"
                             class="w-12"
                        />
                        <div class="text-natural-600 ml-2 w-32">
                            {{ jwtJson.vc.issuer?.name }}
                        </div>
                    </div>
                </div>
            </div>
            <div class="px-7 py-1">
                <div class="text-gray-600 font-bold">
                    {{
                        jwtJson.vc.type[jwtJson.vc.type.length - 1].replace(
                                /([a-z0-9])([A-Z])/g,
                                "$1 $2"
                        )
                    }}
                    Details
                </div>
                <hr class="my-5"/>

                <!-- VerifiableDiploma -->
                <div v-if="jwtJson.vc.type[jwtJson.vc.type.length - 1] == 'VerifiableDiploma'">
                    <div class="text-gray-500 mb-4 font-bold">Subject</div>
                    <div class="md:flex text-gray-500 mb-3 md:mb-1">
                        <div class="min-w-[19vw]">Given Name</div>
                        <div class="font-bold">
                            {{ jwtJson.vc.credentialSubject.givenNames }}
                        </div>
                    </div>
                    <div class="md:flex text-gray-500 mb-3 md:mb-1">
                        <div class="min-w-[19vw]">Family Name</div>
                        <div class="font-bold">
                            {{ jwtJson.vc.credentialSubject.familyName }}
                        </div>
                    </div>
                    <div class="md:flex text-gray-500 mb-3 md:mb-1">
                        <div class="min-w-[19vw]">Date Of Birth</div>
                        <div class="font-bold">
                            {{ jwtJson.vc.credentialSubject.dateOfBirth }}
                        </div>
                    </div>
                    <hr class="my-5"/>
                    <div class="text-gray-500 mb-4 font-bold">Achievement</div>
                    <div class="md:flex text-gray-500 mb-3 md:mb-1">
                        <div class="min-w-[19vw]">Identifier</div>
                        <div class="font-bold">
                            {{ jwtJson.vc.credentialSubject.identifier }}
                        </div>
                    </div>
                    <div class="md:flex text-gray-500 mb-3 md:mb-1">
                        <div class="min-w-[19vw]">Title</div>
                        <div class="font-bold">
                            {{ jwtJson.vc.credentialSubject.learningAchievement.title }}
                        </div>
                    </div>
                    <div class="md:flex text-gray-500 mb-3 md:mb-1">
                        <div class="min-w-[19vw]">Description</div>
                        <div class="font-bold">
                            {{ jwtJson.vc.credentialSubject.learningAchievement.description }}
                        </div>
                    </div>
                    <div class="md:flex text-gray-500 mb-3 md:mb-1">
                        <div class="min-w-[19vw]">Additional Notes</div>
                        <div class="font-bold">
                            {{
                                jwtJson.vc.credentialSubject.learningAchievement
                                        .additionalNote[0]
                            }}
                        </div>
                    </div>
                    <div class="md:flex text-gray-500 mb-3 md:mb-1">
                        <div class="min-w-[19vw]">Grading Scheme</div>
                        <div class="font-bold">
                            {{ jwtJson.vc.credentialSubject.gradingScheme.title }}
                        </div>
                    </div>
                    <div class="md:flex text-gray-500 mb-3 md:mb-1">
                        <div class="min-w-[19vw]">Graduation Location</div>
                        <div class="font-bold">
                            {{ jwtJson.vc.credentialSubject.awardingOpportunity.location }}
                        </div>
                    </div>
                    <div class="md:flex text-gray-500 mb-3 md:mb-1">
                        <div class="min-w-[19vw]">Study Timeframe</div>
                        <div class="font-bold"></div>
                    </div>
                    <hr class="my-5"/>
                    <div class="text-gray-500 mb-4 font-bold">University</div>
                    <div class="md:flex text-gray-500 mb-3 md:mb-1">
                        <div class="min-w-[19vw]">Legal Identifier</div>
                        <div class="font-bold">
                            {{
                                jwtJson.vc.credentialSubject.awardingOpportunity.awardingBody
                                        .eidasLegalIdentifier
                            }}
                        </div>
                    </div>
                    <div class="md:flex text-gray-500 mb-3 md:mb-1">
                        <div class="min-w-[19vw]">Name</div>
                        <div class="font-bold">
                            {{
                                jwtJson.vc.credentialSubject.awardingOpportunity.awardingBody
                                        .preferredName
                            }}
                        </div>
                    </div>
                    <div class="md:flex text-gray-500 mb-3 md:mb-1">
                        <div class="min-w-[19vw]">Registration</div>
                        <div class="font-bold">
                            {{
                                jwtJson.vc.credentialSubject.awardingOpportunity.awardingBody
                                        .registration
                            }}
                        </div>
                    </div>
                </div>

                <!-- Open Badge 3.0 -->
                <div v-if="jwtJson.vc.type[jwtJson.vc.type.length - 1] == 'OpenBadgeCredential'">
                    <div class="flex items-center">
                        <div>
                            <div class="text-gray-500 mb-4 font-bold">Subject</div>
                            <div class="md:flex text-gray-500 mb-3 md:mb-1">
                                <div class="min-w-[19vw]">Name</div>
                                <div class="font-bold">
                                    {{ jwtJson.vc.credentialSubject.achievement.name }}
                                </div>
                            </div>
                            <div class="md:flex text-gray-500 mb-3 md:mb-1">
                                <div class="min-w-[19vw]">Description</div>
                                <div class="font-bold">
                                    {{ jwtJson.vc.credentialSubject.achievement.description }}
                                </div>
                            </div>
                            <div class="md:flex text-gray-500 mb-3 md:mb-1">
                                <div class="min-w-[19vw]">Criteria</div>
                                <div class="font-bold grow-0">
                                    {{
                                        jwtJson.vc.credentialSubject.achievement.criteria.narrative
                                    }}
                                </div>
                            </div>
                        </div>
                        <img :src="jwtJson.vc.credentialSubject.achievement.image" class="w-32 h-20 hidden md:block"/>
                    </div>
                </div>

                <!-- Permanent Resident Card -->
                <div v-else-if="jwtJson.vc.type[jwtJson.vc.type.length - 1] == 'PermanentResidentCard'">
                    <div>
                        <div class="text-gray-500 mb-4 font-bold">Subject Info</div>
                        <div class="md:flex text-gray-500 mb-3 md:mb-1">
                            <div class="min-w-[19vw]">Given Name</div>
                            <div class="font-bold">
                                {{ jwtJson.vc.credentialSubject.givenName }}
                            </div>
                        </div>
                        <div class="md:flex text-gray-500 mb-3 md:mb-1">
                            <div class="min-w-[19vw]">Surname</div>
                            <div class="font-bold">
                                {{ jwtJson.vc.credentialSubject.familyName }}
                            </div>
                        </div>
                        <div class="md:flex text-gray-500 mb-3 md:mb-1">
                            <div class="min-w-[19vw]">Date Of Birth</div>
                            <div class="font-bold grow-0">
                                {{
                                    jwtJson.vc.credentialSubject.birthDate
                                }}
                            </div>
                        </div>
                        <div class="md:flex text-gray-500 mb-3 md:mb-1">
                            <div class="min-w-[19vw]">Sex</div>
                            <div class="font-bold grow-0">
                                {{
                                    jwtJson.vc.credentialSubject.gender
                                }}
                            </div>
                        </div>
                        <div class="md:flex text-gray-500 mb-3 md:mb-1">
                            <div class="min-w-[19vw]">Country Of Birth</div>
                            <div class="font-bold grow-0">
                                {{
                                    jwtJson.vc.credentialSubject.birthCountry
                                }}
                            </div>
                        </div>
                        <div class="md:flex text-gray-500 mb-3 md:mb-1">
                            <div class="min-w-[19vw]">Category</div>
                            <div class="font-bold grow-0">
                                {{
                                    jwtJson.vc.credentialSubject.lprCategory
                                }}
                            </div>
                        </div>
                        <div class="md:flex text-gray-500 mb-3 md:mb-1">
                            <div class="min-w-[19vw]">USCIS</div>
                            <div class="font-bold grow-0">
                                {{
                                    jwtJson.vc.credentialSubject.lprNumber
                                }}
                            </div>
                        </div>
                        <div class="md:flex text-gray-500 mb-3 md:mb-1">
                            <div class="min-w-[19vw]">Resident Since</div>
                            <div class="font-bold grow-0">
                                {{
                                    jwtJson.vc.credentialSubject.residentSince
                                }}
                            </div>
                        </div>
                    </div>
                </div>

                <hr class="my-5"/>
                <div v-if="jwtJson.vc.issuer">
                    <div class="text-gray-500 mb-4 font-bold">Issuer</div>
                    <div class="md:flex text-gray-500 mb-3 md:mb-1">
                        <div class="min-w-[19vw]">Name</div>
                        <div class="font-bold">{{ jwtJson.vc.issuer.name }}</div>
                    </div>
                    <div class="md:flex text-gray-500 mb-3 md:mb-1">
                        <div class="min-w-[19vw]">DID</div>
                        <div class="font-bold overflow-scroll lg:overflow-auto">
                            {{ jwtJson.vc?.issuer?.id ?? jwtJson.vc.issuer }}
                        </div>
                    </div>
                    <hr class="mt-5 mb-3"/>
                    <div class="text-gray-600 flex justify-between">
                        <div>
                            {{
                                jwtJson.vc.expirationDate && jwtJson.vc.issuanceDate
                                ? "Valid from " + new Date(jwtJson.vc.issuanceDate).toISOString().slice(0, 10) + " to " + new
                                    Date(jwtJson.vc.expirationDate).toISOString().slice(0, 10)
                                : ""
                            }}
                        </div>
                        <div class="text-gray-900">
                            Issued
                            {{ jwtJson.vc.issuanceDate ? new Date(jwtJson.vc.issuanceDate).toISOString().slice(0, 10) : "No issuancedate" }}
                        </div>
                    </div>
                </div>
            </div>
            <div class="flex justify-between">
                <div class="mt-12 text-primary-400 cursor-pointer" @click="showCredentialJson = !showCredentialJson">
                    <u>View Credential In JSON</u>
                </div>
                <div class="mt-12 text-red-400 cursor-pointer" @click="deleteCredential">
                    Delete Credential
                </div>
            </div>
            <div v-if="showCredentialJson">
                <div class="flex space-x-3 mt-10">
                    <div class="min-w-0 flex-1">
                        <p class="text-sm font-semibold text-gray-900 whitespace-pre-wrap">
                            {{ credentialId }}
                        </p>
                        <p class="text-sm text-gray-500">
                            Verifiable Credential data below:
                        </p>
                    </div>
                </div>
                <!-- <div class="p-3 shadow mt-3">
                        <h3 class="font-semibold mb-2">QR code</h3>
                        <div v-if="credential && credential.length">
                            <qrcode-vue v-if="credential.length <= 4296" :value="credential" level="L" size="300"></qrcode-vue>
                            <p v-else>Unfortunately, this Verifiable Credential is too big to be viewable as QR code (credential size is {{ credential.length }} characters, but the maximum a QR code
                                can hold is 4296).</p>
                        </div>

                    </div>-->
                <div class="shadow p-3 mt-2 font-mono overflow-scroll">
                    <h3 class="font-semibold mb-2">JWT</h3>
                    <pre
                            v-if="credential && credential.length"
                    >{{
                            /*JSON.stringify(JSON.parse(*/
                            credential /*), null, 2)*/ ?? ""
                        }}</pre>
                </div>
                <div class="shadow p-3 mt-2 font-mono overflow-scroll">
                    <h3 class="font-semibold mb-2">JSON</h3>
                    <pre v-if="credential && credential.length">{{ jwtJson }} </pre>
                </div>
            </div>
        </div>
    </CenterMain>
</template>

<script setup>
import LoadingIndicator from "~/components/loading/LoadingIndicator.vue";
import CenterMain from "~/components/CenterMain.vue";
import BackButton from "~/components/buttons/BackButton.vue";
import {ref} from "vue";
import {decodeBase64ToUtf8} from "~/composables/base64";

const route = useRoute();
const credentialId = route.params.credentialId;

let showCredentialJson = ref(false);

const jwtJson = computed(() => {
    if (credential.value) {
        const vcData = credential.value.split(".")[1]
        console.log("Credential is: ", vcData)

        const vcBase64 = vcData.replaceAll("-", "+").replaceAll("_", "/")
        console.log("Base64 from base64url: ", vcBase64)

        const decodedBase64 = decodeBase64ToUtf8(vcBase64).toString()
        console.log("Decoded: ", decodedBase64)

        return JSON.parse(decodedBase64);
    } else return "";
});

const {
    data: credential,
    pending,
    refresh,
    error,
} = await useLazyFetch(`/r/wallet/credentials/${encodeURIComponent(credentialId)}`);
refreshNuxtData();

useHead({title: "View credential - walt.id"});

async function deleteCredential() {
    await $fetch(`/r/wallet/credentials/${encodeURIComponent(credentialId)}`, {
        method: "DELETE",
    });
    await navigateTo({path: "/"});
}
</script>
