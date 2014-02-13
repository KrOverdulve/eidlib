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
 * The "root verification" exception is thrown when the root certificate of a
 * certain smart card couldn't be verified against the hard coded root
 * certificate.
 * 
 * @author Kristof Overdulve
 * @version 1.0.0 28 Mar 2007
 */
@SuppressWarnings("serial")
public class RootVerificationException extends Exception {

	/**
	 * Initializes the root verification exception.
	 */
	public RootVerificationException() {
		super();
	}

	/**
	 * Initializes the signature verification exception with the given cause of
	 * the problem whose raw data couldn't be verified correctly.
	 * 
	 * @param cause
	 *            is the cause of the exception
	 */
	public RootVerificationException(final Throwable cause) {
		super(cause);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Throwable#getMessage()
	 */
	public String getMessage() {
		return "The root certificate is incorrect.";
	}
}
