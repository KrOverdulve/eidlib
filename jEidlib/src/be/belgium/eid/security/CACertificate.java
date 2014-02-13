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

import javax.smartcardio.CardException;

import be.belgium.eid.eidlib.SmartCard;
import be.belgium.eid.exceptions.CardNotFoundException;
import be.belgium.eid.objects.SmartCardReadable;

/**
 * This class contains the Certificate Authority certificate which is a
 * certificate for verifying data on a smart card.
 * 
 * @author Kristof Overdulve
 * @version 1.0.0 25 Mar 2008
 */
public class CACertificate extends Certificate implements SmartCardReadable {

	/** Contains the CA specific file attributes to read from on the smart card */
	public final static byte[] fgCA = { fgCertTag, fgDataTagCA };

	/** Contains the label for the current certificate */
	public final static String fgLabel = "CA";

	/**
	 * Initializes the CA certificate by reading the data from the given card
	 * reader.
	 * 
	 * @param cardReader
	 *            is the reader used by the CA certificate to fetch the
	 *            certificate data from
	 * @throws CardException
	 * @throws CardNotFoundException
	 * @throws CardNotFoundException
	 *             indicates that the card wasn't present in the system or was
	 *             reset
	 * @throws UnknownErrorException
	 *             when an exception occurred of which the cause is unknown
	 * @throws ReaderDetachedException
	 *             indicates that the reader was detached from the system since
	 *             the connection was made
	 * @throws TransmitFailedException
	 *             indicates that the transmission of the request for the read
	 *             operation failed
	 * @throws FileNotFoundException
	 *             indicates that the file to be read couldn't be found on the
	 *             smart card
	 * @throws BufferTooSmallException
	 *             if the given outputLength is too small
	 * @throws IOException
	 *             when the certificate couldn't be parsed because the file
	 *             system is read only
	 * @throws CertificateException
	 *             when the instance couldn't be parsed
	 */
	public CACertificate(final SmartCard cardReader)
			throws CardNotFoundException, CardException {
		// Initializes the certificate with the read data and the constant label
		super(cardReader.readFile(new byte[] { Certificate.fgDFCert[0],
				Certificate.fgDFCert[1], fgCA[0], fgCA[1] }, fgMAX_CERT_LEN),
				fgLabel);
	}

	/**
	 * Initializes the CA certificate by the given data.
	 * 
	 * @param contents
	 *            are the contents of the certificate
	 */
	public CACertificate(final byte[] contents) {
		super(contents, fgLabel);
	}
}
