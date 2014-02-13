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
 * The eID exception concerns any BE ID specific exceptions. Since much can go
 * wrong when performing actions on a Belgian eID smart card. This class
 * encapsulates more specific errors. These more specific and detailed errors
 * are stored as the cause of the exception.
 * 
 * @author Kristof Overdulve
 * @version 1.0.0 11 Feb 2008
 */
@SuppressWarnings("serial")
public class EIDException extends Exception {

	/**
	 * Initializes the EID exception with the given cause of the exception.
	 * 
	 * @param cause
	 *            is the cause of the exception
	 */
	public EIDException(final Throwable cause) {
		super(cause);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Throwable#getMessage()
	 */
	public String getMessage() {
		return "EID Exception: " + super.getCause().getMessage();
	}
}
