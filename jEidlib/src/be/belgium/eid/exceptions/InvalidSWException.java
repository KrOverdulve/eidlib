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
package be.belgium.eid.exceptions;

/**
 * The "invalid SW" exception indicates that the values 0x90 and 0x00 should
 * have been returned for Status Word (SW) 1 and 2 respectively.
 * 
 * @author Kristof Overdulve
 * @version 1.0.0 25 Nov 2007
 */
@SuppressWarnings("serial")
public class InvalidSWException extends Exception {

	/** contains the invalid SW1 */
	private final int fSW1;

	/** contains the invalid SW2 */
	private final int fSW2;

	/**
	 * Initializes a new invalid SW exception.
	 * 
	 * @param sw1
	 *            is the first invalid status word
	 * @param sw2
	 *            is the second invalid status word
	 */
	public InvalidSWException(final int sw1, final int sw2) {
		super();
		fSW1 = sw1;
		fSW2 = sw2;
	}

	/**
	 * Initializes a new invalid SW exception with the cause of the problem.
	 * 
	 * @param sw1
	 *            is the first invalid status word
	 * @param sw2
	 *            is the second invalid status word
	 * @param cause
	 *            is the cause of the exception
	 */
	public InvalidSWException(final int sw1, final int sw2, Throwable cause) {
		super(cause);
		fSW1 = sw1;
		fSW2 = sw2;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Throwable#getMessage()
	 */
	public String getMessage() {
		return "The returned status word values " + fSW1 + " and " + fSW2
				+ " should have been 0x90 and 0x00.";
	}
}
