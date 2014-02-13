/*
 * 5. LICENSE ISSUES 
 * The eID Toolkit uses several third-party libraries or code. 
 * Redistributions in any form of the eID Toolkit – even embedded in a compiled application – 
 * must reproduce all the eID Toolkit and third-party’s copyright notices, list of conditions, 
 * disclaimers, and any other materials provided with the distribution. 
 * 
 * 5.1 Disclaimer 
 * This eID Toolkit is provided by the Belgian Government “as is”, and any expressed or implied 
 * warranties, including, but not limited to, the implied warranties of merchantability and fitness 
 * for a particular purpose are disclaimed.  In no event shall the Belgian Government or its 
 * contributors be liable for any direct, indirect, incidental, special, exemplary, or consequential 
 * damages (including, but not limited to, procurement of substitute goods or services; loss of use, 
 * data, or profits; or business interruption) however caused and on any theory of liability, whether 
 * in contract, strict liability, or tort (including negligence or otherwise) arising in any way out of 
 * the use of this Toolkit, even if advised of the possibility of such damage. 
 * However, the Belgian Government will ensure the maintenance of the Toolkit – that is, bug 
 * fixing, and support of new versions of the Electronic Identity card.
 * 
 * Source: DeveloperGuide.pdf
 */
package be.belgium.eid.security;

/**
 * Contains the different states that can occur when validating a certificate.
 * 
 * @author Kristof Overdulve
 * @version 1.0.0 03 Dec 2007
 */
public enum CertificateStatus {

	BEID_CERTSTATUS_CERT_VALIDATED_OK, /* Validation has occurred successfully. */

	BEID_CERTSTATUS_CERT_NOT_VALIDATED, /* No validation has been done. */

	BEID_CERTSTATUS_UNABLE_TO_GET_ISSUER_CERT, /* Unable to get issuer certificate */

	BEID_CERTSTATUS_UNABLE_TO_GET_CRL, /* Unable to get certificate CRL */

	BEID_CERTSTATUS_UNABLE_TO_DECRYPT_CERT_SIGNATURE, /* Unable to decrypt certificate's signature */

	BEID_CERTSTATUS_UNABLE_TO_DECRYPT_CRL_SIGNATURE, /* Unable to decrypt CRL's signature */

	BEID_CERTSTATUS_UNABLE_TO_DECODE_ISSUER_PUBLIC_KEY, /* Unable to decode issuer public key */

	BEID_CERTSTATUS_CERT_SIGNATURE_FAILURE, /* Certificate signature failure */

	BEID_CERTSTATUS_CRL_SIGNATURE_FAILURE, /* CRL signature failure */

	BEID_CERTSTATUS_CERT_NOT_YET_VALID, /* Certificate is not yet valid */

	BEID_CERTSTATUS_CERT_HAS_EXPIRED, /* Certificate has expired */

	BEID_CERTSTATUS_CRL_NOT_YET_VALID, /* CRL is not yet valid */

	BEID_CERTSTATUS_CRL_HAS_EXPIRED, /* CRL has expired */

	BEID_CERTSTATUS_ERR_IN_CERT_NOT_BEFORE_FIELD, /* Format error in certificate's notBefore field */

	BEID_CERTSTATUS_ERR_IN_CERT_NOT_AFTER_FIELD, /* Format error in certificate's notAfter field */

	BEID_CERTSTATUS_ERR_IN_CRL_LAST_UPDATE_FIELD, /* Format error in CRL's lastUpdate field */

	BEID_CERTSTATUS_ERR_IN_CRL_NEXT_UPDATE_FIELD, /* Format error in CRL's nextUpdate field */

	BEID_CERTSTATUS_OUT_OF_MEM, /* Out of memory */

	BEID_CERTSTATUS_DEPTH_ZERO_SELF_SIGNED_CERT, /* Self signed certificate */

	BEID_CERTSTATUS_SELF_SIGNED_CERT_IN_CHAIN, /* Self signed certificate in certificate chain */

	BEID_CERTSTATUS_UNABLE_TO_GET_ISSUER_CERT_LOCALLY, /* Unable to get local issuer certificate */

	BEID_CERTSTATUS_UNABLE_TO_VERIFY_LEAF_SIGNATURE, /* Unable to verify the first certificate */

	BEID_CERTSTATUS_CERT_CHAIN_TOO_LONG, /* Certificate chain too long */

	BEID_CERTSTATUS_CERT_REVOKED, /* Certificate revoked  */

	BEID_CERTSTATUS_INVALID_CA, /* Invalid CA certificate */
	
	BEID_CERTSTATUS_INVALID_ROOT, /* Invalid root certificate */

	BEID_CERTSTATUS_PATH_LENGTH_EXCEEDED, /* Path length constraint exceeded */

	BEID_CERTSTATUS_INVALID_PURPOSE, /* Unsupported certificate purpose */

	BEID_CERTSTATUS_CERT_UNTRUSTED, /* Certificate not trusted */

	BEID_CERTSTATUS_CERT_REJECTED, /* Certificate rejected */

	BEID_CERTSTATUS_SUBJECT_ISSUER_MISMATCH, /* Subject issuer mismatch */

	BEID_CERTSTATUS_AKID_SKID_MISMATCH, /* Authority and subject key identifier mismatch */

	BEID_CERTSTATUS_AKID_ISSUER_SERIAL_MISMATCH, /* Authority and issuer serial number mismatch */

	BEID_CERTSTATUS_KEYUSAGE_NO_CERTSIGN, /* Key usage does not include certificate signing */

	BEID_CERTSTATUS_UNABLE_TO_GET_CRL_ISSUER, /* Unable to get CRL issuer certificate */

	BEID_CERTSTATUS_UNHANDLED_CRITICAL_EXTENSION, /* Unhandled critical extension */
	
	BEID_CERTSTATUS_CERT_UNKNOWN /* Unknown error */
}
