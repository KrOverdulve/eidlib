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

/**
 * The certificate chain class represents the chain of certificates that are to
 * be verified to verify the data on a certain smart card. The authentication
 * and signature certificates are verified by the certificate of the Certificate
 * Authority which is by itself again verified against the root certificate
 * which is self-signed (meaning that it can sign it's own validity).
 * 
 * @author Kristof Overdulve
 * @version 1.0.0 25 Mar 2008
 */
public class CertificateChain {

	/** Contains the root certificate at the top of the certificates chain */
	private final RootCertificate fRootCert;

	/** Contains the certificate of the certificate authority */
	private final CACertificate fCACert;

	/** Contains the authentication certificate for the data */
	private final AuthenticationCertificate fAuthCert;

	/** Contains the signature certificate for the data */
	private final SignatureCertificate fSigCert;

	/**
	 * Initializes the certificate chain by reading all the certificates in the
	 * chain from the smart card and to verify them.
	 * 
	 * @param card
	 *            is the smart card to fetch the certificate data from
	 * @throws CardException
	 *             when a card related error occurred
	 * @throws CardNotFoundException
	 *             indicates that the card wasn't present in the system or was
	 *             reset
	 * @throws IOException
	 *             when the certificate couldn't be parsed because the file
	 *             system is read only
	 * @throws CertificateException
	 *             when the instance couldn't be parsed
	 */
	public CertificateChain(final SmartCard card) throws CardNotFoundException,
			CardException, CertificateException, IOException {
		fRootCert = new RootCertificate(card);
		fRootCert.verify();
		fCACert = new CACertificate(card);
		fCACert.verify();
		fAuthCert = new AuthenticationCertificate(card);
		fAuthCert.verify();
		fSigCert = new SignatureCertificate(card);
		fSigCert.verify();
	}

	/**
	 * Initializes the certificate chain by filling the data with existing
	 * certificates.
	 * 
	 * @param rootCert
	 *            is the root certificate
	 * @param caCert
	 *            is the certificate authority certificate
	 * @param authCert
	 *            is the authentication certificate
	 * @param sigCert
	 *            is the signature certificate
	 */
	public CertificateChain(final RootCertificate rootCert,
			final CACertificate caCert,
			final AuthenticationCertificate authCert,
			final SignatureCertificate sigCert) {
		fRootCert = rootCert;
		fCACert = caCert;
		fAuthCert = authCert;
		fSigCert = sigCert;
	}

	/**
	 * Returns the root certificate at the top of the certificates chain.
	 * 
	 * @return the root certificate
	 */
	public RootCertificate getRootCert() {
		return fRootCert;
	}

	/**
	 * Returns the certificate of the certificate authority.
	 * 
	 * @return the CA certificate
	 */
	public CACertificate getCertificateAuthorityCert() {
		return fCACert;
	}

	/**
	 * Returns the authentication certificate.
	 * 
	 * @return the auth certif
	 */
	public AuthenticationCertificate getAuthenticationCert() {
		return fAuthCert;
	}

	/**
	 * Returns the signature certificate.
	 * 
	 * @return the signature certif
	 */
	public SignatureCertificate getSignatureCert() {
		return fSigCert;
	}
}
