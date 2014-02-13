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
 * The unknown error exception is thrown when an unknown or unhandled exception
 * was thrown. The error codes -1900 (which indicates that the card itself
 * returned that an error occurred that was unknown for it) and -1901 (which
 * indicates that the PKCS#15 application couldn't be found) are also put in
 * this exception.
 * 
 * @author Kristof Overdulve
 * @version 1.0.0 17 Dec 2007
 */
@SuppressWarnings("serial")
public class UnknownErrorException extends Exception {

	/** Contains the error code that was returned and that was unknown */
	private final int fErrorCode;

	/**
	 * Initializes the exception with the given error code
	 * 
	 * @param errorCode
	 *            is the error code that was returned by the reader
	 */
	public UnknownErrorException(final int errorCode) {
		super();
		fErrorCode = errorCode;
	}

	/**
	 * Initializes the exception with the given cause of the exception
	 * 
	 * @param errorCode
	 *            is the error code that was returned by the reader
	 * @param cause
	 *            is the cause of the exception
	 */
	public UnknownErrorException(final Throwable cause, final int errorCode) {
		super(cause);
		fErrorCode = errorCode;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Throwable#getMessage()
	 */
	public String getMessage() {
		return "The error with code " + fErrorCode
				+ " that occurred is unknown.";
	}

}
