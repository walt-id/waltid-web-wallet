package id.walt.service.keri.interfaces

import id.walt.service.dto.IPEX_EVENT
import id.walt.service.dto.IpexSaid

/**
 * IPEX protocol is a protocol for the issuance and presentation of ACDCs
 * that enable contractually protected disclosure. A discloser of an ACDC
 * may initially disclose the content of the ACDC partially or selectively
 * and may reveal more content after the disclosee agrees to contractual terms.
 *
 *
 * The Workflow of IPEX is the following:
 * Step 1: Apply [Disclosee]
 * Step 2A: Spurn [Discloser]
 * Step 2B: Offer [Discloser]
 * Step 3A: Spurn [Disclosee]
 * Step 3B: Agree [Disclosee]
 * Step 4A: Spurn [Discloser]
 * Step 4B: Grant [Discloser]
 * Step 5: Admit [Disclosee]
 */
interface IpexInterface {
    /**
     * Accept a credential being issued or presented in response to an IPEX grant
     */
    fun admit(keystore: String, alias: String, passcode: String, said: String, message: String?);

    /**
     * Reply to IPEX offer message acknowledged willingness to accept offered credential
     */
    fun agree(): IpexSaid;

    /**
     * Request a credential from another party by initiating an IPEX exchange
     */
    fun apply(): IpexSaid;

    /**
     * Reply to IPEX agree message or initiate an IPEX exchange with a credential issuance or presentation
     */
    fun grant(keystore: String, alias: String, passcode: String, said: String, recipient: String, message: String?): IpexSaid;

    /**
     * List notifications related to IPEX protocol messages
     */
    fun list(keystore: String, alias: String, passcode: String, type: IPEX_EVENT, verbose: Boolean, poll: Boolean, sent: Boolean);

    /**
     * Reply to IPEX apply message or initiate an IPEX exchange with an offer for a credential with certain characteristics
     */
    fun offer(): IpexSaid;

    /**
     * Reject an IPEX apply, offer, agree or grant message
     */
    fun spurn(keystore: String, alias: String, passcode: String, said: String, message: String?);
}

