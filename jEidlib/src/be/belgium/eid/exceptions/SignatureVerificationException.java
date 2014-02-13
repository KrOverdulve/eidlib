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
package be.belgium.eid.exceptions;

/**
 * The "signature verification" exception is thrown when certain data couldn't
 * be verified against it's signature using a public key or that the
 * verification turned out to be unsuccessful because of a mismatch between the
 * data and signature.
 * 
 * @author Kristof Overdulve
 * @version 1.0.0 28 Mar 2007
 */
@SuppressWarnings("serial")
public class SignatureVerificationException extends Exception {

	/** Contains the name of the object that couldn't be verified correctly */
	private final String fObjectName;

	/**
	 * Initializes the signature verification exception with the given object
	 * whose raw data couldn't be verified correctly.
	 * 
	 * @param objectName
	 *            is the name of the object whose raw data couldn't be verified
	 */
	public SignatureVerificationException(final String objectName) {
		super();
		fObjectName = objectName;
	}

	/**
	 * Initializes the signature verification exception with the given object
	 * and the given cause of the problem whose raw data couldn't be verified
	 * correctly.
	 * 
	 * @param objectName
	 *            is the name of the object whose raw data couldn't be verified
	 * @param cause
	 *            is the cause of the exception
	 */
	public SignatureVerificationException(final Throwable cause,
			final String objectName) {
		super(cause);
		fObjectName = objectName;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Throwable#getMessage()
	 */
	public String getMessage() {
		return "The data of the " + fObjectName
				+ "  couldn't be verified correctly against it's signature";
	}
}
