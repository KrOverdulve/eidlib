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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Date;

import be.belgium.eid.objects.SmartCardReadable;

/**
 * The certificate class is an abstract class for the different kinds of
 * certificates stored on the smart card. Certificates can be used for verifying
 * whether the data stored on a smart card matches the certificates by means of
 * authorization.
 * 
 * @author Kristof Overdulve
 * @version 1.0.0 12 Feb 2008
 */
public abstract class Certificate implements Verifiable, SmartCardReadable {

	/** Contains the third and fourth file attributes to read a certificate */
	public final static byte[] fgDFCert = { (byte) 0xDF, (byte) 0x00 };

	/** Contains the maximum length that a certificate can be */
	public static final int fgMAX_CERT_LEN = 2048;

	/** Contains the maximum length of an ID */
	public static final int fgMAX_CERT_ID_LEN = 256;

	/** Contains the contents of the certificate */
	private final byte[] fContents;

	/** Contains the ID of the certificate */
	private final String fID;

	/** Contains the status of the certificate */
	private CertificateStatus fStatus;

	/**
	 * Initializes the certificate with the given contents and identification.
	 * 
	 * @param contents
	 *            are the contents of the certificate
	 * @param ID
	 *            is the identification for the certificate
	 */
	public Certificate(final byte[] contents, final String ID) {
		// Preconditions
		assert (ID.length() <= fgMAX_CERT_ID_LEN);
		assert (contents.length <= fgMAX_CERT_LEN);

		fContents = contents.clone();
		fID = ID;
		fStatus = CertificateStatus.BEID_CERTSTATUS_CERT_NOT_VALIDATED;
	}

	/**
	 * Returns the contents of the certificate.
	 * 
	 * @return the contents
	 */
	public byte[] getContents() {
		return fContents.clone();
	}

	/**
	 * Returns the ID of the certificate.
	 * 
	 * @return the ID
	 */
	public String getID() {
		return fID;
	}

	/**
	 * Sets the status of the certificate.
	 * 
	 * @param status
	 *            is the status for the certificate
	 */
	public void setStatus(final CertificateStatus status) {
		fStatus = status;
	}

	/**
	 * Returns the status of the certificate.
	 * 
	 * @return the status
	 */
	public CertificateStatus getStatus() {
		return fStatus;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see be.belgium.eid.security.Verifiabele#verify()
	 */
	public boolean verify() throws CertificateException, IOException {
		if (this.getStatus().equals(
				CertificateStatus.BEID_CERTSTATUS_CERT_VALIDATED_OK)) {
			// Already verified
			return true;
		} else {
			if (this.getX509Certificate().getNotBefore().after(new Date())) {
				this
						.setStatus(CertificateStatus.BEID_CERTSTATUS_CERT_NOT_YET_VALID);
				return false;
			} else if (this.getX509Certificate().getNotAfter().before(
					new Date())) {
				this
						.setStatus(CertificateStatus.BEID_CERTSTATUS_CERT_HAS_EXPIRED);
				return false;
			} else {
				// Everything fine so don't change
				return true;
			}
		}
	}

	/**
	 * Returns the parsed X509 certificate from the encoded RN certificate
	 * fetched from the smart card.
	 * 
	 * @return the parsed X509 certificate
	 * @throws IOException
	 *             when the certificate couldn't be parsed because the file
	 *             system is read only
	 * @throws CertificateException
	 *             when the instance couldn't be parsed
	 */
	public X509Certificate getX509Certificate() throws IOException,
			CertificateException {
		ByteArrayInputStream bais = new ByteArrayInputStream(this.getContents());
		final CertificateFactory cf = CertificateFactory.getInstance("X.509");
		final X509Certificate cert = (X509Certificate) cf
				.generateCertificate(bais);

		return cert;
	}
}