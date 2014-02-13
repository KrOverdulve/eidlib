/*
 * 5. LICENSE ISSUES 
 * The eID Toolkit uses several third-party libraries or code. 
 * Redistributions in any form of the eID Toolkit � even embedded in a compiled application � 
 * must reproduce all the eID Toolkit and third-party�s copyright notices, list of conditions, 
 * disclaimers, and any other materials provided with the distribution. 
 * 
 * 5.1 Disclaimer 
 * This eID Toolkit is provided by the Belgian Government �as is�, and any expressed or implied 
 * warranties, including, but not limited to, the implied warranties of merchantability and fitness 
 * for a particular purpose are disclaimed.  In no event shall the Belgian Government or its 
 * contributors be liable for any direct, indirect, incidental, special, exemplary, or consequential 
 * damages (including, but not limited to, procurement of substitute goods or services; loss of use, 
 * data, or profits; or business interruption) however caused and on any theory of liability, whether 
 * in contract, strict liability, or tort (including negligence or otherwise) arising in any way out of 
 * the use of this Toolkit, even if advised of the possibility of such damage. 
 * However, the Belgian Government will ensure the maintenance of the Toolkit � that is, bug 
 * fixing, and support of new versions of the Electronic Identity card.
 * 
 * Source: DeveloperGuide.pdf
 */
package be.belgium.eid.security;

import java.io.IOException;
import java.security.cert.CertificateException;

import javax.smartcardio.CardException;

import be.belgium.eid.eidlib.SmartCard;
import be.belgium.eid.exceptions.CardNotFoundException;
import be.belgium.eid.objects.SmartCardReadable;

/**
 * The RN certificate is the (National Register) certificate for verifying
 * identities. The RRN signs the address file together with the identity file to
 * guarantee the link between these two files
 * 
 * @author Kristof Overdulve
 * @version 1.0.0 12 Feb 2008
 */
public class RNCertificate extends Certificate implements SmartCardReadable {

	/** Contains the RN specific file attributes to read from on the smart card */
	public final static byte[] fgRN = { fgCertTag, fgDataTagRN };

	/** Contains the subject name identifiers */
	public final static String fgDN_RRN = "RRNRRNBE";

	/** Contains the default label for an RN certificate */
	public final static String fgLabel = "RN";

	/**
	 * Initializes the RN certificate.
	 * 
	 * @param card
	 *            is the smart card to fetch the certificate data from
	 * @throws CardException
	 *             when a card related error occurred
	 * @throws CardNotFoundException
	 *             indicates that the card wasn't present in the system or was
	 *             reset
	 */
	public RNCertificate(final SmartCard card) throws CardNotFoundException,
			CardException {
		// Initializes the certificate with the read data and the constant label
		super(card.readFile(new byte[] { Certificate.fgDFCert[0],
				Certificate.fgDFCert[1], fgRN[0], fgRN[1] }, fgMAX_CERT_LEN),
				fgLabel);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see be.belgium.eid.security.Certificate#verify()
	 */
	@Override
	public boolean verify() throws CertificateException, IOException {
		if (super.verify()) {

			// First verify the DN
			// Parse the principal subject to remove the "CN=", "O=" and "C="
			final String rawSubject = super.getX509Certificate()
					.getSubjectX500Principal().getName();
			final String cn = rawSubject.substring(
					rawSubject.indexOf("CN=") + 3).split(",")[0];
			final String o = rawSubject.substring(rawSubject.indexOf("O=") + 2)
					.split(",")[0];
			final String c = rawSubject.substring(rawSubject.indexOf("C=") + 2)
					.split(",")[0];

			// Returns whether they're equal
			return fgDN_RRN.equals(cn + o + c);
		} else {
			return false;
		}
	}
}
